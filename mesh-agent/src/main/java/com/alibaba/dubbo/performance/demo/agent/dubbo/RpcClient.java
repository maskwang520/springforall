package com.alibaba.dubbo.performance.demo.agent.dubbo;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.*;
import com.alibaba.dubbo.performance.demo.agent.util.ServerLoopMap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.function.Consumer;

public class RpcClient {

    private final static Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);

    private ConnecManager connectManager = new ConnecManager();
    private ServerLoopMap eventLoopMap = new ServerLoopMap();
    private ServerLoopMap map = new ServerLoopMap();


    public void invoke(String interfaceName, String method, String parameterTypesString, String parameter,int requestId,Consumer callback,EventLoop loop) throws Exception {

        Channel channel = map.get(loop);

        RpcInvocation invocation = new RpcInvocation();
        invocation.setMethodName(method);
        invocation.setAttachment("path", interfaceName);
        invocation.setParameterTypes(parameterTypesString);    // Dubbo内部用"Ljava/lang/String"来表示参数类型是String

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));
        JsonUtils.writeObject(parameter, writer);
        invocation.setArguments(out.toByteArray());


        Request request = new Request();
        request.setVersion("2.0.0");
        request.setTwoWay(true);
        request.setData(invocation);
        request.setId(requestId);
        RpcFuture future = new RpcFuture(callback);
        RpcRequestHolder.put(String.valueOf(request.getId()),future);
        LOGGER.info("requestId=" + request.getId());
        channel.writeAndFlush(request);

    }
}
