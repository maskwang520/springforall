package com.alibaba.dubbo.performance.demo.agent.netty.handler;

import com.alibaba.dubbo.performance.demo.agent.HelloController;
import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClient;
import com.alibaba.dubbo.performance.demo.agent.netty.model.RequestWrapper;
import com.alibaba.dubbo.performance.demo.agent.netty.model.ResponseWrapper;
import com.alibaba.dubbo.performance.demo.agent.registry.RegistryInstance;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServerHandler extends SimpleChannelInboundHandler<RequestWrapper>{
    private Logger logger = LoggerFactory.getLogger(HelloController.class);
    private RpcClient rpcClient = new RpcClient(RegistryInstance.getInstance());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestWrapper req) throws Exception {
        ResponseWrapper response = new ResponseWrapper();
        response.requestId = req.requestId;
        try {
            String result = handle(req);
            response.result = result;
        } catch (Exception e) {
            logger.error("handle result failure", e);
        }
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private String handle(RequestWrapper req) {
        rpcClient.invoke(req.interfaceName, req.method, req.parameterTypesString, req.parameter, );
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
