package com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.handler;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcResponse;
import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.connectionpool.NettyPoolClient;
import com.alibaba.dubbo.performance.demo.agent.util.ChannelContextHolder;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by maskwang on 18-6-3.
 * consumer-agent的逻辑
 */
public class ClientHandler extends SimpleChannelInboundHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class);
    private static NettyPoolClient client = new NettyPoolClient();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        RpcResponse agentRpcResponse = (RpcResponse) msg;

        ChannelHandlerContext channelContext = ChannelContextHolder.getChannelContext((Integer.valueOf(agentRpcResponse.getRequestId())));
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(new String(agentRpcResponse.getBytes()), CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        LOGGER.info("the return requestid is {}", agentRpcResponse.getRequestId());
//        if(channelContext==null){
//            LOGGER.info("channelcontext is null");
//        }
        if(channelContext!=null) {
            ChannelContextHolder.removeChannelContext((Integer.valueOf(agentRpcResponse.getRequestId())));
            channelContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }


    }
}
