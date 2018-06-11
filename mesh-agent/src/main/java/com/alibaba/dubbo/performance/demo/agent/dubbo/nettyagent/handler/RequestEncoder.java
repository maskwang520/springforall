package com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.handler;

import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.modle.RequestProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by maskwang on 18-6-5.
 * 请求编码器
 */
public class RequestEncoder extends MessageToByteEncoder<RequestProtocol> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RequestProtocol requsestProtocol, ByteBuf byteBuf) throws Exception {

        if(requsestProtocol!=null) {
            byteBuf.writeInt(requsestProtocol.getRequestId());
            byteBuf.writeBytes(requsestProtocol.getContent());
        }
    }
}
