package com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.handler;

import com.alibaba.dubbo.performance.demo.agent.channel.ConsumerAgentChannel;
import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.connectionpool.AgentClientPool;
import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.modle.RegistrySingleton;
import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.registry.IRegistry;
import com.alibaba.dubbo.performance.demo.agent.util.ChannelContextHolder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by maskwang on 18-6-9.
 */
public class NettyServerHandler extends SimpleChannelInboundHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerHandler.class);
    private static AtomicInteger count = new AtomicInteger(0);

    private AgentClientPool agentClientPool = new AgentClientPool();
    private  Random random = new Random();
    private static List<Endpoint> endpoints = null;
    IRegistry registry = RegistrySingleton.getInstance();


    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        FullHttpRequest httpRequest = (FullHttpRequest) msg;
        try {
            ByteBuf buf = httpRequest.content();    //获取参数
            ByteBuf byteBuf = Unpooled.directBuffer();

            if (null == endpoints) {
                synchronized (ConsumerAgentChannel.class) {
                    if (null == endpoints) {
                        endpoints = registry.find("com.alibaba.dubbo.performance.demo.provider.IHelloService");
                        ListIterator<Endpoint> it = endpoints.listIterator();
                        while (it.hasNext()) {
                            Endpoint temp = it.next();
                            if (temp.getSize() == 2) {
                                it.add(temp);
                            }
                        }
                    }
                }
            }

            // 简单的负载均衡，随机取一个
            Endpoint endpoint = endpoints.get(random.nextInt(endpoints.size()));
            Channel channel = agentClientPool.getChannel(endpoint.getHost(),endpoint.getPort(),ctx.channel().eventLoop());
            int requestId = count.getAndIncrement();

            //LOGGER.info("THE INPUT ID IS {}", requestId);
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



}
