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
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by maskwang on 18-6-14.
 */
public class AgentClientPool {
    private static EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
    private static Bootstrap bootstrap;
    static List<Channel> channels = new ArrayList<>();
    static AtomicInteger count = new AtomicInteger(0);

    public Channel getChannel(String host, Integer port, EventLoopGroup workGroup) {
        //eventLoopGroup = workGroup;
        if (null == bootstrap) {
            synchronized (AgentClientPool.class) {
                if (null == bootstrap) {
                    initBootstrap();
                }
            }
        }
        if (channels.size() < 12) {
            synchronized (AgentClientPool.class) {
                if (channels.size() < 12) {
                    try {
                        channels.add(bootstrap.connect(host, port).sync().channel());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return channels.get(count.getAndIncrement() % channels.size());

    }

    public void initBootstrap() {

        bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new ResponseDecoder())
                                .addLast(new ClientHandler());
                    }
                });
    }


}
