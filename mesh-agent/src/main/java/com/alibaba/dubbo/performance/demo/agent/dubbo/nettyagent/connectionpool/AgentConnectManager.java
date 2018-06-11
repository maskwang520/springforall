package com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.connectionpool;

import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.handler.ClientHandler;
import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.handler.ResponseDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by maskwang on 18-6-7.
 */
public class AgentConnectManager {
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);

    private Bootstrap bootstrap;

    private Channel channel;
    private Object lock = new Object();

    private String host;
    private int  port;

    public AgentConnectManager(String host,int port) {
        this.host = host;
        this.port = port;
    }

    public Channel getChannel() throws Exception {
        if (null != channel) {
            return channel;
        }

        if (null == bootstrap) {
            synchronized (lock) {
                if (null == bootstrap) {
                    initBootstrap();
                }
            }
        }

        if (null == channel) {
            synchronized (lock){
                if (null == channel){
                    channel = bootstrap.connect(host, port).sync().channel();
                }
            }
        }

        return channel;
    }

    public void initBootstrap() {

        bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
//                        ch.pipeline().addLast(new RequestEncoder());
                        ch.pipeline().addLast(new ResponseDecoder());
                        ch.pipeline().addLast(new ClientHandler());
                    }
                });
    }
}