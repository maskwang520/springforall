package com.alibaba.dubbo.performance.demo.agent.dubbo;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 创建dubbo clinet的channel
 *
 */

public class ConnecManager {
    private static EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
    private static Bootstrap bootstrap;


    public ConnecManager() {
    }

    public Channel getChannel(String host, int port) throws Exception {
        Channel channel = null;
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
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class)
                .handler(new RpcClientInitializer());
    }
}
