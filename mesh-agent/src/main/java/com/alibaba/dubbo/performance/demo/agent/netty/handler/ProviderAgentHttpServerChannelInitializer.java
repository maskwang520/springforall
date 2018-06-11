package com.alibaba.dubbo.performance.demo.agent.netty.handler;

import com.alibaba.dubbo.performance.demo.agent.netty.Server.ProviderAgentServer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * Created this one by huminghao on 2018/6/11.
 */
public class ProviderAgentHttpServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //channel.config().setAllocator(UnpooledByteBufAllocator.DEFAULT);
        //channel.config().setKeepAlive(true);
        //channel.config().setTcpNoDelay(true);
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpRequestDecoder());
        pipeline.addLast("codec", new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(512 * 1024));
        //pipeline.addLast(new LoggingHandler(LogLevel.INFO));
        pipeline.addLast(new ProviderAgentServer());
    }
}
