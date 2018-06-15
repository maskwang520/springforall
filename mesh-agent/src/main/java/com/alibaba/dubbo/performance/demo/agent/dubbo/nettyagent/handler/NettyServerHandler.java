package com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.handler;

import com.alibaba.dubbo.performance.demo.agent.dubbo.ClientAgentConnectManager;
import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.modle.RegistrySingleton;
import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.util.ChannelContextHolder;
import com.alibaba.dubbo.performance.demo.agent.util.EventLoopMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;

import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by maskwang on 18-6-9.
 */
public class NettyServerHandler extends SimpleChannelInboundHandler {

    private static AtomicInteger count = new AtomicInteger(0);
    private static AtomicInteger counter = new AtomicInteger(0);

    private ClientAgentConnectManager manager = new ClientAgentConnectManager();
    private EventLoopMap eventLoopMap = new EventLoopMap();
    private Object lock = new Object();
    private static List<Endpoint> endpoints = null;


    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        FullHttpRequest httpRequest = (FullHttpRequest) msg;
        try {
            ByteBuf buf = httpRequest.content();    //获取参数
            ByteBuf byteBuf = Unpooled.directBuffer();
            Channel channel = getChannel(ctx.channel().eventLoop());
            int requestId = count.getAndIncrement();
            //保存通道,方便返回的时候直接取
            ChannelContextHolder.putChannelContext(requestId, ctx);

            byteBuf.writeInt(buf.readableBytes() + 4);
            byteBuf.writeInt(requestId);
            byteBuf.writeBytes(buf);

            ReferenceCountUtil.retain(buf);
            channel.writeAndFlush(byteBuf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Channel getChannel(EventLoop loop) throws Exception {
        //加权轮询负载均衡
        if (null == endpoints) {
            synchronized (lock) {
                if (null == endpoints) {
                    endpoints = RegistrySingleton.getInstance().find("com.alibaba.dubbo.performance.demo.provider.IHelloService");
                    ListIterator<Endpoint> it = endpoints.listIterator();
                    while (it.hasNext()) {
                        Endpoint temp = it.next();
                        if (temp.getSize() == 2) {
                            it.add(temp);
                            it.add(temp);
                            it.add(temp);
                            it.add(temp);
                        }
                    }
                }
            }
        }
        int id = counter.getAndIncrement();
        if (id >= 4) {
            counter.set(0);
            id = 4;
        }

        Endpoint endpoint = endpoints.get(id);
        Channel channel = null;
        if (eventLoopMap.contains(loop)) {
            channel = eventLoopMap.get(loop);
        } else {
            channel = manager.getChannel(endpoint.getHost(), endpoint.getPort());
            //把eventloop和channel联系在一起
            eventLoopMap.put(loop, channel);
        }

        return channel;
    }


}
