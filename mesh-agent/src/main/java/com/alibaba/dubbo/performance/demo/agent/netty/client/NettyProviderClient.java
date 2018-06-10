package com.alibaba.dubbo.performance.demo.agent.netty.client;

import com.alibaba.dubbo.performance.demo.agent.netty.handler.ClientHandler;
import com.alibaba.dubbo.performance.demo.agent.netty.model.RequestEncoder;
import com.alibaba.dubbo.performance.demo.agent.netty.model.RequestWrapper;
import com.alibaba.dubbo.performance.demo.agent.netty.model.ResponseDecoder;
import com.alibaba.dubbo.performance.demo.agent.netty.model.ResponseWrapper;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created this one by huminghao on 2018/6/9.
 */

public class NettyProviderClient {

    private Logger logger  = LoggerFactory.getLogger(NettyProviderClient.class);

    public void connect(String url, int port, RequestWrapper requestWrapper) {
        EventLoopGroup group;
        Bootstrap bootstrap;
        group = new NioEventLoopGroup();
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.config().setKeepAlive(true);
                            ch.config().setTcpNoDelay(true);
                            ch.config().setAllocator(PooledByteBufAllocator.DEFAULT);
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new RequestEncoder(RequestWrapper.class));
                            pipeline.addLast(new ResponseDecoder(ResponseWrapper.class));
                            pipeline.addLast(new ClientHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect(url.substring(7,url.lastIndexOf(":")), port).sync();
            future.channel().writeAndFlush(requestWrapper);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
