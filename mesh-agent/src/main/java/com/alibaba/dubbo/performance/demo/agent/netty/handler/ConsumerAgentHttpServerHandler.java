package com.alibaba.dubbo.performance.demo.agent.netty.handler;

import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.registry.RegistryInstance;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;

import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created this one by huminghao on 2018/6/9.
 */
public class ConsumerAgentHttpServerHandler extends ChannelInboundHandlerAdapter {

    private static List<Endpoint> endpoints = null;

    private Channel outboundChannel;

    private static AtomicInteger count = new AtomicInteger(0);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (null == endpoints) {
            synchronized (ConsumerAgentHttpServerHandler.class) {
                if (null == endpoints) {
                    endpoints = RegistryInstance.getInstance().find("com.alibaba.dubbo.performance.demo.provider.IHelloService");
                    ListIterator<Endpoint> it = endpoints.listIterator();
                    while (it.hasNext()){
                        Endpoint temp = it.next();
                        if(temp.getSize()==2) {
                            it.add(temp);
                            it.add(temp);
                            it.add(temp);
                            it.add(temp);
                        }
                    }
                }
            }
        }
        int id = count.getAndIncrement();
        if(id>=4){
            count.set(0);
            id=4;
        }
        // 简单的负载均衡，随机取一个
        Endpoint endpoint = endpoints.get(id);

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
                        ch.pipeline().addLast(new ConsumerAgentHttpClientHandler(inboundChannel));
                    }
                })
                .option(ChannelOption.AUTO_READ, false);
        ChannelFuture f = b.connect(endpoint.getHost(), endpoint.getPort());

        outboundChannel = f.channel();
        f.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                // connection complete start to read first data
                inboundChannel.read();
            }
        });
    }

    /**
     * Closes the specified channel after all queued write requests are flushed.
     */
    static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER);
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