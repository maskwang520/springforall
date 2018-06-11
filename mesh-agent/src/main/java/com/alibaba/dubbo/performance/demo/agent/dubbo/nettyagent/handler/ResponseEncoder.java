package com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.handler;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by maskwang on 18-6-5.
 * 响应编码器
 */
public class ResponseEncoder extends MessageToByteEncoder<RpcResponse> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcResponse agentRpcResponse, ByteBuf byteBuf) throws Exception {
        if(agentRpcResponse!=null) {
            byteBuf.writeInt(Integer.valueOf(agentRpcResponse.getRequestId()));
            byteBuf.writeBytes(agentRpcResponse.getBytes());
        }
    }
}
