package com.alibaba.dubbo.performance.demo.agent.netty.handler;

import com.alibaba.dubbo.performance.demo.agent.netty.model.RequestEncoder;
import com.alibaba.dubbo.performance.demo.agent.netty.model.ResponseDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class ServerConnector {
//    private final static Logger LOGGER = LoggerFactory.getLogger(ServerConnector.class);
//
//    public void connet(int port) {
//        EventLoopGroup bossGroup = new NioEventLoopGroup();
//        EventLoopGroup workerGroup = new NioEventLoopGroup(4);
//        try {
//            ServerBootstrap sbs = new ServerBootstrap().group(bossGroup, workerGroup)
//                    .channel(NioServerSocketChannel.class)
//                    .localAddress(new InetSocketAddress(port))
//                    .childHandler(new ChannelInitializer<SocketChannel>() {
//                        protected void initChannel(SocketChannel channel) throws Exception {
//                            channel.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(2048));
//                            channel.pipeline().addLast(new RequestEncoder());
//                            channel.pipeline().addLast(new ResponseDecoder());
//                            channel.pipeline().addLast(new ServerHandler());
//                        };
//                    }).option(ChannelOption.SO_BACKLOG, 200)
//                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
//                    .option(ChannelOption.TCP_NODELAY, true)
//                    .childOption(ChannelOption.SO_KEEPALIVE, true);
//            LOGGER.info("netty server start");
//            // 绑定端口，开始接收进来的连接
//            ChannelFuture future = sbs.bind(port).sync();
//            future.channel().closeFuture().sync();
//        } catch (Exception e) {
//            bossGroup.shutdownGracefully();
//            workerGroup.shutdownGracefully();
//        }
//    }

}
