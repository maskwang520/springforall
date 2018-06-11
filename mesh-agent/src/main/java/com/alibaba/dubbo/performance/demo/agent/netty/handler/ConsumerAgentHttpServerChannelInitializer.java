package com.alibaba.dubbo.performance.demo.agent.netty.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * Created this one by huminghao on 2018/6/11.
 */
public class ConsumerAgentHttpServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        //channel.config().setKeepAlive(true);
        //channel.config().setTcpNoDelay(true);
        channel.config().setAutoRead(false);
        //pipeline.addLast(new HttpRequestDecoder());
        //pipeline.addLast(new HttpResponseEncoder());
        channel.pipeline().addLast(new ConsumerAgentHttpServerHandler());
    }
}
