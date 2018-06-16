package com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.handler;

import com.alibaba.dubbo.performance.demo.agent.dubbo.ClientAgentConnectManager;
import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.modle.RegistrySingleton;
import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.util.ChannelContextHolder;
import com.alibaba.dubbo.performance.demo.agent.util.ClientLoopMap;
import com.alibaba.dubbo.performance.demo.agent.util.HttpParser;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by maskwang on 18-6-9.
 */
public class NettyServerHandler extends SimpleChannelInboundHandler {

    private static AtomicInteger count = new AtomicInteger(0);
    private static AtomicInteger counter = new AtomicInteger(0);

    private static List<Integer> nums = Arrays.asList(0);
    private static ClientLoopMap map = new ClientLoopMap();


    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        FullHttpRequest httpRequest = (FullHttpRequest) msg;
        Map<String, String> map = HttpParser.parse(httpRequest);
        try {

            //ByteBuf byteBuf = ctx.channel().alloc().buffer();
            Channel channel = getChannel(ctx.channel().eventLoop());
            int requestId = count.getAndIncrement();
            //保存ctx,方便返回的时候直接取
            ChannelContextHolder.putChannelContext(requestId, ctx);

            String interfaceName = map.get("interface");
            int lena = interfaceName.length();

            String methodName = map.get("method");
            int lenb = methodName.length();

            String parameterType = map.get("parameterTypesString");
            int lenc = parameterType.length();

            String parameter = map.get("parameter");
            int lend = parameter.length();


//            channel.write(lena + lenb + lenc + lend + 20);
//            channel.write(requestId);
//            channel.write(lena);
//            channel.write(interfaceName.getBytes());
//            channel.write(lenb);
//            channel.write(methodName.getBytes());
//            channel.write(lenc);
//            channel.write(parameterType.getBytes());
//            channel.write(lend);

//            channel.write(parameter.getBytes());
            //ReferenceCountUtil.retain(byteBuf);
//            channel.writeAndFlush(parameter.getBytes());
            channel.writeAndFlush("shgfjgdfgjdfhgdfg");
            System.out.println("sbbb");
//            System.out.println(channel.remoteAddress().toString());
           // ctx.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Channel getChannel(EventLoop loop) throws Exception {

//        int id = counter.getAndIncrement();
//        if (id >= 4) {
//            counter.set(0);
//            id = 4;
//        }
        List<Channel> list = map.get(loop);
        return list.get(nums.get(0));
    }


}
