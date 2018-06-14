package com.alibaba.dubbo.performance.demo.agent.dubbo;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcFuture;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcRequestHolder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcResponse;
import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.handler.NettyServerHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerHandler.class);


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse response) {
        String requestId = response.getRequestId();
        ByteBuf byteBuf = Unpooled.directBuffer();
        byteBuf.writeInt(response.getBytes().length + 4);
        byteBuf.writeInt(Integer.valueOf(response.getRequestId()));
        byteBuf.writeBytes(response.getBytes());
        RpcFuture future = RpcRequestHolder.get(requestId);
        if(null != future){
            RpcRequestHolder.remove(requestId);
            future.done(byteBuf);
        }
    }

}
