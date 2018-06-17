package com.alibaba.dubbo.performance.demo.agent.dubbo;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.*;
import com.alibaba.dubbo.performance.demo.agent.util.EventLoopMap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoop;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.function.Consumer;

public class RpcClient {

    private static EventLoopMap map = new EventLoopMap();


    public void invoke(String interfaceName, String method, String parameterTypesString, String parameter, Consumer<String> callback, EventLoop loop) throws Exception {
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

        RpcFuture future = new RpcFuture(callback);
        RpcRequestHolder.put(String.valueOf(request.getId()),future);

        channel.writeAndFlush(request);
    }
}