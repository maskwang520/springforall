package com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.handler;

import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.modle.RequestProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by maskwang on 18-6-5.
 */
public class RequestDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        RequestProtocol requestProtocol = new RequestProtocol();

        if (byteBuf.readableBytes() < 4) {
            return;
        }
        int biginIndex = byteBuf.readerIndex();
        int len = byteBuf.readInt();

        if (byteBuf.readableBytes() < len) {
            byteBuf.readerIndex(biginIndex);
            return;
        }

        int requestId = byteBuf.readInt();
        byte[] temp = new byte[len - 4];
        byteBuf.readBytes(temp);
        requestProtocol.setRequestId(requestId);
        requestProtocol.setContent(temp);
        list.add(requestProtocol);

    }
}
