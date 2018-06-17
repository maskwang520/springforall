package com.alibaba.dubbo.performance.demo.agent;

import com.alibaba.dubbo.performance.demo.agent.dubbo.ClientAgentConnectManager;
import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.handler.NettyServerHandler;
import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.modle.RegistrySingleton;
import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.util.ClientLoopMap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.concurrent.EventExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by maskwang on 18-6-9.
 * http server端
 */
public class ConsumerHttpServer {

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup(4);
    private ServerBootstrap b = new ServerBootstrap();
    private ClientLoopMap map = new ClientLoopMap();
    private List<Endpoint> endpoints;
    private ClientAgentConnectManager manager = new ClientAgentConnectManager();

    public void getConsumerChannle(int port) throws InterruptedException {
        putMap();
        Channel channel = null;
        try {
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline ph = ch.pipeline();
                    //处理http服务的关键handler
                    ph.addLast("encoder", new HttpResponseEncoder());
                    ph.addLast("decoder", new HttpRequestDecoder());
                    ph.addLast("aggregator", new HttpObjectAggregator(512 * 1024));
                    ph.addLast("handler", new NettyServerHandler());// 服务端业务逻辑
                }
            });
            channel = b.bind(port).sync().channel();
            channel.closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }


    /**
     * 提前构造channel放到map中
     */
    public void putMap() {
        try {
            endpoints = RegistrySingleton.getInstance().find("com.alibaba.dubbo.performance.demo.provider.IHelloService");
            //排序
            Collections.sort(endpoints, new Comparator<Endpoint>() {
                @Override
                public int compare(Endpoint o1, Endpoint o2) {
                    return o1.getSize()>o2.getSize()?1:(o1.getSize()==o1.getSize()?0:-1);
                }
            });

            for (EventExecutor executor : workerGroup) {
                ArrayList<Channel> arrayList = new ArrayList();
                //可以改变个数
                for (int i = 0; i < 3; i++) {
                    arrayList.add(manager.getChannel(endpoints.get(i).getHost(), endpoints.get(i).getPort(), (EventLoop) executor));
                }
                map.put((EventLoop) executor, arrayList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
