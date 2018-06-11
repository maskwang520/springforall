package com.alibaba.dubbo.performance.demo.agent;

import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.handler.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * Created by maskwang on 18-6-9.
 * http server端
 */
public class ConsumerHttpServer {

    private static EventLoopGroup group = new NioEventLoopGroup();
    private static ServerBootstrap b = new ServerBootstrap();

    public Channel getConsumerChannle(int port) throws InterruptedException {
        Channel channel = null;
        try {
            b.group(group)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline ph = ch.pipeline();
                    //处理http服务的关键handler
                    ph.addLast("encoder", new HttpResponseEncoder());
                    ph.addLast("decoder", new HttpRequestDecoder());
                    ph.addLast("aggregator", new HttpObjectAggregator(10 * 1024 * 1024));
                    ph.addLast("handler", new NettyServerHandler());// 服务端业务逻辑
                }
            }); //设置过滤器
            // 服务器绑定端口监听
            channel = b.bind(port).sync().channel();
            //System.out.println("服务端启动成功,端口是:" + port);
            channel.closeFuture().sync();
            // 监听服务器关闭监听

        } finally {
            group.shutdownGracefully(); //关闭EventLoopGroup，释放掉所有资源包括创建的线程
        }

        return channel;
    }
}
