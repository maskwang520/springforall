package com.alibaba.dubbo.performance.demo.agent.netty.handler;

import io.netty.channel.*;

@ChannelHandler.Sharable
public class ConsumerAgentHttpClientHandler extends ChannelInboundHandlerAdapter {

    private  Channel inboundChannel;

    public ConsumerAgentHttpClientHandler(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.read();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {

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
        ConsumerAgentHttpServerHandler.closeOnFlush(inboundChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ConsumerAgentHttpServerHandler.closeOnFlush(ctx.channel());

    }
}