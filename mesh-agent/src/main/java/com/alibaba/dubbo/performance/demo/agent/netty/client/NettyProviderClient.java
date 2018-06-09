package com.alibaba.dubbo.performance.demo.agent.netty.client;

import com.alibaba.dubbo.performance.demo.agent.netty.handler.ClientHandler;
import com.alibaba.dubbo.performance.demo.agent.netty.model.RequestEncoder;
import com.alibaba.dubbo.performance.demo.agent.netty.model.RequestWrapper;
import com.alibaba.dubbo.performance.demo.agent.netty.model.ResponseDecoder;
import com.alibaba.dubbo.performance.demo.agent.netty.model.ResponseWrapper;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Consumer;

/**
 * Created this one by huminghao on 2018/6/9.
 */

public class NettyProviderClient {

    private Logger logger  = LoggerFactory.getLogger(NettyProviderClient.class);

    public void connect(String url, int port, RequestWrapper requestWrapper, Consumer<String> consumer) {
        EventLoopGroup group;
        Bootstrap bootstrap;
        group = new NioEventLoopGroup();
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new RequestEncoder(RequestWrapper.class));
                            pipeline.addLast(new ResponseDecoder(ResponseWrapper.class));
                            pipeline.addLast(new ClientHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect(url, port).sync();
            Channel channel = future.channel();
            channel.writeAndFlush(requestWrapper).sync();
            channel.closeFuture().sync();
            logger.info("channel connected");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }




}
