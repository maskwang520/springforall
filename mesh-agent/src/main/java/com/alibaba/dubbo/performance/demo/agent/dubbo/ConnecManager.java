package com.alibaba.dubbo.performance.demo.agent.dubbo;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ConnecManager {
    private EventLoopGroup eventLoopGroup ;
    private Bootstrap bootstrap;


    public Channel getChannel(EventLoopGroup group) throws Exception {
        this.eventLoopGroup = group;
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
                    int port = Integer.valueOf(System.getProperty("dubbo.protocol.port"));
                    channel = bootstrap.connect("127.0.0.1", port).sync().channel();
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
