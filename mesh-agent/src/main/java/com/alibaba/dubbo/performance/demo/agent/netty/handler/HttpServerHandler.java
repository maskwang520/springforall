package com.alibaba.dubbo.performance.demo.agent.netty.handler;

import com.alibaba.dubbo.performance.demo.agent.netty.client.NettyProviderClient;
import com.alibaba.dubbo.performance.demo.agent.netty.model.*;
import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.registry.RegistryInstance;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
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
public class HttpServerHandler extends ChannelInboundHandlerAdapter {

    private static List<Endpoint> endpoints = null;

    private Channel outboundChannel;

    private Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (null == endpoints) {
            synchronized (lock) {
                if (null == endpoints) {
                    endpoints = RegistryInstance.getInstance().find("com.alibaba.dubbo.performance.demo.provider.IHelloService");
                }
            }
        }

        // 简单的负载均衡，随机取一个
        Endpoint endpoint = endpoints.get(random.nextInt(endpoints.size()));
        final Channel inboundChannel = ctx.channel();
        //
        // Start the connection attempt.
        Bootstrap b = new Bootstrap();
        b.group(inboundChannel.eventLoop())
                .channel(ctx.channel().getClass())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
//                        ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));

                        ch.pipeline().addLast(new ClientHandler(inboundChannel));
                    }
                })
                .option(ChannelOption.AUTO_READ, false);
        ChannelFuture f = b.connect(endpoint.getHost(), endpoint.getPort());

        outboundChannel = f.channel();
        f.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                // connection complete start to read first data
                inboundChannel.read();
            } else {
                // Close the connection if the connection attempt has failed.
                inboundChannel.close();
            }
        });
    }

    /**
     * Closes the specified channel after all queued write requests are flushed.
     */
    static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }else{
            ch.close();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (outboundChannel.isActive()) {
            outboundChannel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    ctx.channel().read();
                } else {
                    future.channel().close();
                }
            });
        }
    }

    private Random random = new Random();
    private Object lock = new Object();
    private NettyProviderClient nettyProviderClient = new NettyProviderClient();

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (outboundChannel != null) {
            closeOnFlush(outboundChannel);
        }
    }

//public void consumer(String interfaceName, String method, String parameterTypesString, String parameter, Consumer<String> consumer) throws Exception {
    //
    //
    //
    //    String url = "http://" + endpoint.getHost() + ":" + endpoint.getPort();
    //    RequestWrapper requestWrapper = new RequestWrapper(interfaceName, method, parameterTypesString, parameter);
    //
    //    NettyRequestHolder.put(requestWrapper.requestId, consumer);
    //    nettyProviderClient.connect(url, endpoint.getPort(), requestWrapper);
    //
    //}


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        closeOnFlush(ctx.channel());
    }


}
