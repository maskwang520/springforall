package com.alibaba.dubbo.performance.demo.agent.dubbo;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcResponse;
import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.handler.NettyServerHandler;
import com.alibaba.dubbo.performance.demo.agent.util.DubboChannelContextHolder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerHandler.class);


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse response) {
        Integer requestId = Integer.valueOf(response.getRequestId());
        //获取持有的上一个ctx

        ByteBuf byteBuf = channelHandlerContext.channel().alloc().directBuffer();
        byteBuf.writeInt(response.getBytes().length + 4);
        byteBuf.writeInt(Integer.valueOf(response.getRequestId()));
        byteBuf.writeBytes(response.getBytes());
        //System.out.println(holder.getChannelContext(Integer.valueOf(requestId)).channel().remoteAddress().toString());
        ChannelHandlerContext handlerContext = DubboChannelContextHolder.getChannelContext((requestId));
        if(handlerContext!=null) {
            handlerContext.writeAndFlush(byteBuf).addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if (future.isSuccess()) {
                        DubboChannelContextHolder.removeChannelContext(requestId);
                    }
                }
            });
        }


    }

}
