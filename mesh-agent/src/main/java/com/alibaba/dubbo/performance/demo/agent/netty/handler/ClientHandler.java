package com.alibaba.dubbo.performance.demo.agent.netty.handler;

import com.alibaba.dubbo.performance.demo.agent.netty.model.NettyRequestHolder;
import com.alibaba.dubbo.performance.demo.agent.netty.model.ResponseWrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientHandler extends SimpleChannelInboundHandler<ResponseWrapper> {

    private Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponseWrapper resp) throws Exception {
        logger.info("get resp: " + resp.result);
        NettyRequestHolder.get(resp.requestId).accept(resp.result);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("get error:", cause);
        ctx.close();
    }
}
