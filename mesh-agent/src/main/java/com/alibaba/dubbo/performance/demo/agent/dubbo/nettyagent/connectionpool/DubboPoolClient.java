package com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.connectionpool;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.*;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * Created by maskwang on 18-6-9.
 */
public class DubboPoolClient {
    final EventLoopGroup group = new NioEventLoopGroup(8);
    final Bootstrap strap = new Bootstrap();

    public ChannelPoolMap<InetSocketAddress, SimpleChannelPool> poolMap;

    public DubboPoolClient(){
        strap.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT);
        poolMap = new AbstractChannelPoolMap<InetSocketAddress, SimpleChannelPool>() {
            @Override
            protected SimpleChannelPool newPool(InetSocketAddress key) {
                return new FixedChannelPool(strap.remoteAddress(key), new DubboChannelPoolHandler(),64);
            }
        };
    }
}
