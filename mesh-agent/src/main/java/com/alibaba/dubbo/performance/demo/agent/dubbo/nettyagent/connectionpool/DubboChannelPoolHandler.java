package com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.connectionpool;

import com.alibaba.dubbo.performance.demo.agent.dubbo.DubboRpcDecoder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.DubboRpcEncoder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClientHandler;
import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.SocketChannel;

/**
 * Created by maskwang on 18-6-9.
 */
public class DubboChannelPoolHandler implements ChannelPoolHandler {

    @Override
    public void channelReleased(Channel ch) throws Exception {

    }

    @Override
    public void channelAcquired(Channel ch) throws Exception {

    }

    @Override
    public void channelCreated(Channel ch) throws Exception {
        SocketChannel channel = (SocketChannel) ch;
        channel.config().setKeepAlive(true);
        channel.config().setTcpNoDelay(true);
        channel.pipeline().addLast(new DubboRpcEncoder());
        channel.pipeline().addLast(new DubboRpcDecoder());
        channel.pipeline().addLast(new RpcClientHandler());

    }
}