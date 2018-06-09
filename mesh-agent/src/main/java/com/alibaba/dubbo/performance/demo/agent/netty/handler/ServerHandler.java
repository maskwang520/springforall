package com.alibaba.dubbo.performance.demo.agent.netty.handler;

import com.alibaba.dubbo.performance.demo.agent.HelloController;
import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClient;
import com.alibaba.dubbo.performance.demo.agent.netty.model.NettyRequestHolder;
import com.alibaba.dubbo.performance.demo.agent.netty.model.RequestWrapper;
import com.alibaba.dubbo.performance.demo.agent.netty.model.ResponseWrapper;
import com.alibaba.dubbo.performance.demo.agent.registry.RegistryInstance;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.function.Consumer;

public class ServerHandler extends SimpleChannelInboundHandler<RequestWrapper> {
    private Logger logger = LoggerFactory.getLogger(HelloController.class);
    private RpcClient rpcClient = new RpcClient(RegistryInstance.getInstance());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestWrapper req) {
        ResponseWrapper response = new ResponseWrapper();
        response.requestId = req.requestId;
        try {
            handle(req, (result)->{
                response.result = result;
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            });
        } catch (Exception e) {
            logger.error("handle result failure", e);
        }

    }

    private String handle(RequestWrapper req, Consumer<String> callback) {
        rpcClient.invoke(req.interfaceName, req.method, req.parameterTypesString, req.parameter, , callback);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
