package com.alibaba.dubbo.performance.demo.agent.netty.handler;

import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.registry.RegistryInstance;
import com.alibaba.dubbo.performance.demo.agent.util.AgentConnectManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

/**
 * Created this one by huminghao on 2018/6/9.
 */
public class ConsumerAgentHttpServerHandler extends ChannelInboundHandlerAdapter {

    private static List<Endpoint> endpoints = null;

    private Channel outboundChannel;

    private AgentConnectManager manager = new AgentConnectManager();

    private static final Logger logger = LoggerFactory.getLogger(ConsumerAgentHttpServerHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (null == endpoints) {
            synchronized (lock) {
                if (null == endpoints) {
                    endpoints = RegistryInstance.getInstance().find("com.alibaba.dubbo.performance.demo.provider.IHelloService");
                    endpoints.add(endpoints.get(1));
                    endpoints.add(endpoints.get(2));
                }
            }
        }

        // 简单的负载均衡，随机取一个
        Endpoint endpoint = endpoints.get(random.nextInt(endpoints.size()));
        final Channel inboundChannel = ctx.channel();

        // Start the connection attempt.
        Bootstrap b = new Bootstrap();
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(8);
        b.group(eventLoopGroup )
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
//                        ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                        ch.pipeline().addLast(new ConsumerAgentHttpClientHandler(inboundChannel));
                    }
                })
                .option(ChannelOption.AUTO_READ, false);
        ChannelFuture f = b.connect(endpoint.getHost(), endpoint.getPort());
       // ChannelFuture f = manager.getChannel(endpoint.getHost(),endpoint.getPort());
        outboundChannel = f.channel();
       // ChannelMap.map.put("/"+endpoint.getHost()+":"+endpoint.getPort(),inboundChannel);
        f.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                // connection complete start to read first data
                inboundChannel.read();
            } else {
                // Close the connection if the connection attempt has failed.
                //inboundChannel.close();
            }
        });
    }

    /**
     * Closes the specified channel after all queued write requests are flushed.
     */
    static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER);
        }else{
            //ch.close();
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
