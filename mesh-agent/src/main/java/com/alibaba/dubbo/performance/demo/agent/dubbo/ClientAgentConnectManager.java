package com.alibaba.dubbo.performance.demo.agent.dubbo;

import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.handler.ClientHandler;
import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.handler.ResponseDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by maskwang on 18-6-15.
 * <p>
 * 构造clientAgent连接providentAgent的channel
 */
public class ClientAgentConnectManager {

    private  EventLoopGroup eventLoopGroup;
    private  Bootstrap bootstrap;


    public ClientAgentConnectManager() {
    }

    public Channel getChannel(String host, int port,EventLoopGroup loop) throws Exception {
        this.eventLoopGroup = loop;
        Channel channel = null;
        bootstrap = null;
        if (null == bootstrap) {
            synchronized (ClientAgentConnectManager.class) {
                if (null == bootstrap) {
                    initBootstrap();
                }
            }
        }

        if (null == channel) {
            synchronized (ClientAgentConnectManager.class) {
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
