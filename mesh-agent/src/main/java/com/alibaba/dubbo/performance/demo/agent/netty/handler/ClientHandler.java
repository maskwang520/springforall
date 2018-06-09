package com.alibaba.dubbo.performance.demo.agent.netty.handler;

import com.alibaba.dubbo.performance.demo.agent.netty.model.NettyRequestHolder;
import com.alibaba.dubbo.performance.demo.agent.netty.model.ResponseWrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<ResponseWrapper> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponseWrapper resp) throws Exception {
        NettyRequestHolder.get(resp.requestId).accept(resp.result);
        NettyRequestHolder.remove(resp.requestId);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
