package com.alibaba.dubbo.performance.demo.agent.dubbo;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 创建dubbo clinet的channel
 */

public class ConnecManager {
    private EventLoop group;
    private Bootstrap bootstrap;
    public  Channel channel;

    public Channel getChannel(String host, int port, EventLoop loop) throws Exception {
        this.group= loop;
        channel = null;
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
                .group(group)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class)
                .handler(new RpcClientInitializer());
    }
}
