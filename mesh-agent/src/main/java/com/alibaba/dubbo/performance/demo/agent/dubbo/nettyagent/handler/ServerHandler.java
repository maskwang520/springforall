package com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.handler;

import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClient;
import com.alibaba.dubbo.performance.demo.agent.util.DubboChannelContextHolder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.modle.RequestProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by maskwang on 18-6-3.
 * provider-agent的逻辑处理
 */
public class ServerHandler extends SimpleChannelInboundHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);

    private RpcClient rpcClient = new RpcClient();


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        RequestProtocol requestProtocol = (RequestProtocol) msg;

        DubboChannelContextHolder.putChannelContext(requestProtocol.getRequestId(), ctx);

        QueryStringDecoder queryStringDecoder = new QueryStringDecoder("?"+new String(requestProtocol.getContent()));
        Map<String,List<String>> map = queryStringDecoder.parameters();

        //调用
        rpcClient.invoke(map.get("interface").get(0), map.get("method").get(0), map.get("parameterTypesString").get(0), map.get("parameter").get(0),requestProtocol.getRequestId());
        //rpcClient.invoke(values[0], values[1], values[2], values[3]);

    }
}
