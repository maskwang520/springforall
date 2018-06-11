package com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.handler;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by maskwang on 18-6-5.
 * 响应解码器
 */
public class ResponseDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        if(byteBuf.readableBytes()<4) {
            return;
        }
        int readerIndex = byteBuf.readerIndex();
        int len = byteBuf.readInt();
        if(byteBuf.readableBytes()<len) {
            byteBuf.readerIndex(readerIndex);
            return;
        }

        RpcResponse agentRpcResponse = new RpcResponse();
        agentRpcResponse.setRequestId(String.valueOf(byteBuf.readInt()));
        byte[] temp = new byte[len-4];
        byteBuf.readBytes(temp);
        agentRpcResponse.setBytes(temp);
        list.add(agentRpcResponse);



    }
}
