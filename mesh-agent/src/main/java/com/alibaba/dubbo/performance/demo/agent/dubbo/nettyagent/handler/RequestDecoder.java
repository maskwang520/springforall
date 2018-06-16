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
        System.out.println("dasab");
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
        requestProtocol.setRequestId(requestId);

        int lena = byteBuf.readInt();
        byte[] temp = new byte[lena];
        byteBuf.readBytes(temp);
        requestProtocol.setInterfaceName(new String(temp));

        int lenb = byteBuf.readInt();
        temp = new byte[lenb];
        byteBuf.readBytes(temp);
        requestProtocol.setMethodName(new String(temp));

        int lenc = byteBuf.readInt();
        temp = new byte[lenc];
        byteBuf.readBytes(temp);
        requestProtocol.setParameterType(new String(temp));

        int lend = byteBuf.readInt();
        temp = new byte[lend];
        byteBuf.readBytes(temp);
        requestProtocol.setParam(new String(temp));
        list.add(requestProtocol);

       // byteBuf.release();

    }
}
