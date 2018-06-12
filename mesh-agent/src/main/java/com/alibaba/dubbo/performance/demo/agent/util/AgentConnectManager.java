package com.alibaba.dubbo.performance.demo.agent.util;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by maskwang on 18-6-12.
 */
public class AgentConnectManager {
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(8);

    private Bootstrap bootstrap;

    private Channel channel;

    private ChannelFuture channelFuture;
    private Object lock = new Object();

    public AgentConnectManager() {
    }

    public ChannelFuture getChannel(String host, int port) throws Exception {
        if (null != channel) {
            return channelFuture;
        }

        if (null == bootstrap) {
            synchronized (lock) {
                if (null == bootstrap) {
                    initBootstrap();
                }
            }
        }

        if (null == channel) {
            synchronized (lock) {
                if (null == channel) {
                    channelFuture = bootstrap.connect(host, port).sync();
                }
            }
        }

        return channelFuture;
    }

    public void initBootstrap() {

        bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class);
//                .handler(new ConsumerAgentHttpClientHandler());
    }
}