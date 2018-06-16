package com.alibaba.dubbo.performance.demo.agent.dubbo;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 创建dubbo clinet的channel
 *
 */

public class ConnecManager {
    private  EventLoopGroup eventLoopGroup ;
    private  Bootstrap bootstrap;


    public Channel getChannel(String host, int port,EventLoopGroup loop) throws Exception {
        this.eventLoopGroup = loop;
        Channel channel = null;
        bootstrap = null;
        if (null == bootstrap) {
            synchronized (ConnecManager.class) {
                if (null == bootstrap) {
                    initBootstrap();
                }
            }
        }

        if (null == channel) {
            synchronized (ConnecManager.class) {
                if (null == channel) {
                    channel = bootstrap.connect(host, port).channel();
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
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class)
                .handler(new RpcClientInitializer());
    }
}
