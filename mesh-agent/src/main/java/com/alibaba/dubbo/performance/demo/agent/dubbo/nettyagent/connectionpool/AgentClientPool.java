package com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.connectionpool;

import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.handler.ClientHandler;
import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.handler.ResponseDecoder;
import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.List;
import java.util.Random;


/**
 * Created by maskwang on 18-6-14.
 */
public class AgentClientPool {

    private EventLoopGroup eventLoopGroup ;
    private Random random = new Random();
    private EventLoopMap eventLoopMap = new EventLoopMap();

    public void putClientPool(List<Endpoint> endpoints, EventLoopGroup eventLoopGroup) {
        this.eventLoopGroup = eventLoopGroup;
        EventLoop loop = eventLoopGroup.next();
        while (loop != null) {
            Endpoint endpoint = endpoints.get(random.nextInt(endpoints.size()));
            eventLoopMap.put(loop, initChannel(endpoint.getHost(),endpoint.getPort()));
            loop = loop.next();
        }
    }

    public void putServerPool(EventLoopGroup eventLoopGroup) {
        this.eventLoopGroup = eventLoopGroup;
        EventLoop loop = eventLoopGroup.next();
        while (loop != null) {
            eventLoopMap.put(loop, initChannel("127.0.0.1",Integer.valueOf(System.getProperty("dubbo.protocol.port"))));
            loop = loop.next();
        }
    }

    public Channel initChannel(String host, Integer port) {
        Channel channel = null;
        Bootstrap bootstrap = new Bootstrap()
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
        try {
            channel = bootstrap.connect(host, port).sync().channel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channel;
    }


}
