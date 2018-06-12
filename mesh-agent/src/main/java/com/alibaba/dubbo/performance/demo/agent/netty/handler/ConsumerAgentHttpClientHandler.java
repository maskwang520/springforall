package com.alibaba.dubbo.performance.demo.agent.netty.handler;

import com.alibaba.dubbo.performance.demo.agent.util.ChannelMap;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerAgentHttpClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerAgentHttpClientHandler.class);

    //private  Channel inboundChannel;

//    public ConsumerAgentHttpClientHandler(Channel inboundChannel) {
//        this.inboundChannel = inboundChannel;
//    }
    public ConsumerAgentHttpClientHandler() {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.read();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel inboundChannel = ChannelMap.map.get(ctx.channel().remoteAddress().toString());
        inboundChannel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                ctx.channel().read();
            } else {
                future.channel().close();
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel inboundChannel = ChannelMap.map.get(ctx.channel().remoteAddress().toString());
        ConsumerAgentHttpServerHandler.closeOnFlush(inboundChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ConsumerAgentHttpServerHandler.closeOnFlush(ctx.channel());

    }
}
