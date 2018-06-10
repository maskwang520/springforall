package com.alibaba.dubbo.performance.demo.agent.netty.handler;

import com.alibaba.dubbo.performance.demo.agent.netty.client.NettyProviderClient;
import com.alibaba.dubbo.performance.demo.agent.netty.model.NettyRequestHolder;
import com.alibaba.dubbo.performance.demo.agent.netty.model.RequestWrapper;
import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.registry.RegistryInstance;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.handler.codec.http.cookie.Cookie;

import java.net.URI;
import java.util.*;
import java.util.function.Consumer;

import static org.apache.http.cookie.SM.COOKIE;

/**
 * Created this one by huminghao on 2018/6/9.
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    private HttpRequest request;

    private boolean readingChunks;

    private final StringBuilder responseContent = new StringBuilder();

    private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); //Disk

    private HttpPostRequestDecoder decoder;

    private Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    //@Override
    //protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    //    FullHttpRequest httpRequest = (FullHttpRequest) msg;
    //    Map<String, String> data = HttpParser.parse(httpRequest);
    //
    //    consumer(data.get("interfaceName"), data.get("method"), data.get("parameterTypesString"),data.get("parameter"),(result) -> ctx.writeAndFlush(result).addListener(ChannelFutureListener.CLOSE));
    //}

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

    }

    private Random random = new Random();
    private List<Endpoint> endpoints = null;
    private Object lock = new Object();
    private NettyProviderClient nettyProviderClient = new NettyProviderClient();


    public void consumer(String interfaceName, String method, String parameterTypesString, String parameter, Consumer<String> consumer) throws Exception {

        if (null == endpoints) {
            synchronized (lock) {
                if (null == endpoints) {
                    endpoints = RegistryInstance.getInstance().find("com.alibaba.dubbo.performance.demo.provider.IHelloService");
                }
            }
        }

        // 简单的负载均衡，随机取一个
        Endpoint endpoint = endpoints.get(random.nextInt(endpoints.size()));

        String url = "http://" + endpoint.getHost() + ":" + endpoint.getPort();
        RequestWrapper requestWrapper = new RequestWrapper(interfaceName, method, parameterTypesString, parameter);

        NettyRequestHolder.put(requestWrapper.requestId, consumer);
        nettyProviderClient.connect(url, endpoint.getPort(), requestWrapper);

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); //Disk

    private HttpPostRequestDecoder decoder;


}
