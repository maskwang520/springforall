package com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.handler;

import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClient;
import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.modle.RequestProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by maskwang on 18-6-3.
 * provider-agent的逻辑处理
 */
public class ServerHandler extends SimpleChannelInboundHandler<RequestProtocol> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);

    private RpcClient rpcClient = new RpcClient();


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestProtocol requestProtocol) throws Exception {
        System.out.println("wocaonima");
        //调用dubbo client
        rpcClient.invoke(requestProtocol.getInterfaceName(), requestProtocol.getMethodName(), requestProtocol.getParameterType(), requestProtocol.getParam(),requestProtocol.getRequestId(),
                (result)->{
                    ctx.writeAndFlush(result);
                }
       ,ctx.channel().eventLoop());

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("active");
        super.channelActive(ctx);
    }
}
