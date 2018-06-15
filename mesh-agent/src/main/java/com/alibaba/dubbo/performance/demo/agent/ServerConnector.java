package com.alibaba.dubbo.performance.demo.agent;

import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.handler.RequestDecoder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.handler.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Created by maskwang on 18-6-3.
 * pa的server 启动
 */
public class ServerConnector {
    static EventLoopGroup bossGroup = new NioEventLoopGroup();
    static EventLoopGroup workerGroup = new NioEventLoopGroup(4);
    private final static Logger LOGGER = LoggerFactory.getLogger(ServerConnector.class);

    public void connect(int port) {
        try {
            ServerBootstrap sbs = new ServerBootstrap().group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new RequestDecoder());
                            ch.pipeline().addLast(new ServerHandler());
                        }

                    })
                    .option(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            LOGGER.info("server start");
            // 绑定端口，开始接收进来的连接
            ChannelFuture future = sbs.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
