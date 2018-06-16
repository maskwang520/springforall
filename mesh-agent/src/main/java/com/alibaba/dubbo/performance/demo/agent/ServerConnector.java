package com.alibaba.dubbo.performance.demo.agent;

import com.alibaba.dubbo.performance.demo.agent.dubbo.ConnecManager;
import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.handler.RequestDecoder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.handler.ServerHandler;
import com.alibaba.dubbo.performance.demo.agent.util.ServerLoopMap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.EventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Created by maskwang on 18-6-3.
 * pa的server 启动
 */
public class ServerConnector {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServerConnector.class);

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup(4);
    private ServerLoopMap map = new ServerLoopMap();
    private ConnecManager connecManager = new ConnecManager();

    public void connect(int port) {
        putMap();
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
                    .option(ChannelOption.SO_KEEPALIVE, true);
            LOGGER.info("server start:",port);
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
    //构造channel放到池子里
    public void putMap(){
        for(EventExecutor executor:workerGroup){
            try {
                map.put((EventLoop) executor, connecManager.getChannel("127.0.0.1", Integer.valueOf(System.getProperty("dubbo.protocol.port")), (EventLoop) executor));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
