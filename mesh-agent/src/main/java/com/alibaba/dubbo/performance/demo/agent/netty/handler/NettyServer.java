package com.alibaba.dubbo.performance.demo.agent.netty.handler;

import com.alibaba.dubbo.performance.demo.agent.netty.model.RequestEncoder;
import com.alibaba.dubbo.performance.demo.agent.netty.model.RequestWrapper;
import com.alibaba.dubbo.performance.demo.agent.netty.model.ResponseDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
public class NettyServer implements ApplicationContextAware, InitializingBean {
    private final static Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);

    private void providerServerStart(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup(4);
        try {
            ServerBootstrap sbs = new ServerBootstrap().group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel channel) {
                            //channel.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(2048));
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new RequestEncoder(RequestWrapper.class));
                            pipeline.addLast(new ResponseDecoder(ResponseDecoder.class));
                            pipeline.addLast(new ServerHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 200)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            LOGGER.info("netty server start");
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

    private void consumerServerStart(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup(4);
        try {
            ServerBootstrap sbs = new ServerBootstrap().group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel channel) {
                            //channel.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(2048));
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new HttpResponseEncoder());
                            pipeline.addLast(new HttpRequestDecoder());
                            pipeline.addLast(new HttpServerHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            LOGGER.info("netty server start");
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

    @Override
    public void afterPropertiesSet() throws Exception {
        String type = System.getProperty("type");   // 获取type参数
        if ("consumer".equals(type)) {

        }else{
            providerServerStart(19980);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
