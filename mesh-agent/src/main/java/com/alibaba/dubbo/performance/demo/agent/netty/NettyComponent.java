package com.alibaba.dubbo.performance.demo.agent.netty;

import com.alibaba.dubbo.performance.demo.agent.dubbo.ConnecManager;
import com.alibaba.dubbo.performance.demo.agent.netty.handler.ConsumerAgentHttpServerChannelInitializer;
import com.alibaba.dubbo.performance.demo.agent.netty.handler.ProviderAgentHttpServerChannelInitializer;
import com.alibaba.dubbo.performance.demo.agent.registry.RegistryInstance;
import com.alibaba.dubbo.performance.demo.agent.util.EventLoopMap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.EventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
public class NettyComponent implements ApplicationContextAware, InitializingBean {
    private  final static Logger LOGGER = LoggerFactory.getLogger(NettyComponent.class);
    private static EventLoopMap map = new EventLoopMap();
    private ConnecManager connecManager = new ConnecManager();

    private void consumerServerStart(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup(3);
        try {
            ServerBootstrap b = new ServerBootstrap().group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ConsumerAgentHttpServerChannelInitializer());
            LOGGER.info("consumer netty server start");
            ChannelFuture future = b.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private void providerServerStart(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup(4);
        putMap(workerGroup);
        try {
            ServerBootstrap sbs = new ServerBootstrap().group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ProviderAgentHttpServerChannelInitializer());
            LOGGER.info("provider netty server start");
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
            consumerServerStart(20000);
        } else {
            RegistryInstance.getInstance();
            providerServerStart(19980);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    }

    //预先把channel设置好
    public void putMap(EventLoopGroup group) {
        for (EventExecutor executor : group) {
            try {
                map.put((EventLoop) executor, connecManager.getChannel( (EventLoop) executor));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}