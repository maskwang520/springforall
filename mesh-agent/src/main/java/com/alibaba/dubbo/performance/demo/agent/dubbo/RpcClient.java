package com.alibaba.dubbo.performance.demo.agent.dubbo;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.JsonUtils;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.Request;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcInvocation;
import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.connectionpool.DubboPoolClient;
import io.netty.channel.Channel;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;

public class RpcClient {

    private final static Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);

    private static DubboPoolClient client = new DubboPoolClient();
    private static InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", Integer.valueOf(System.getProperty("dubbo.protocol.port")));
    private static SimpleChannelPool pool = client.poolMap.get(inetSocketAddress);


    //private ConnecManager connectManager;


//    public RpcClient(){
//        this.connectManager = new ConnecManager();
//    }

    public static void invoke(String interfaceName, String method, String parameterTypesString, String parameter) throws Exception {

        Channel channel = pool.acquire().get();

        //Channel channel = connectManager.getChannel();

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

        LOGGER.info("requestId=" + request.getId());


        channel.writeAndFlush(request).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if(future.isSuccess()) {
                    pool.release(channel);
                }

            }
        });

    }
}
