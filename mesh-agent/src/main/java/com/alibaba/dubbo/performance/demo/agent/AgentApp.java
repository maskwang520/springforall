package com.alibaba.dubbo.performance.demo.agent;

import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.connectionpool.AgentClientPool;
import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.handler.NettyServerHandler;
import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.modle.RegistrySingleton;
import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.registry.IRegistry;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ListIterator;
import java.util.Random;


/**
 * 程序入口类
 */

public class AgentApp {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentApp.class);
    static AgentClientPool pool = new AgentClientPool();

    public static void main(String[] args) throws Exception {

        String type = System.getProperty("type");
        IRegistry registry = RegistrySingleton.getInstance();
        if ("provider".equals(type)) {
            EventLoopGroup group = new EpollEventLoopGroup(16);
            serverInit(group);
            new ServerConnector(group).connect(19980);
            LOGGER.info("start provider");
        } else {
            EventLoopGroup group = new EpollEventLoopGroup(16);
            clientInit(registry,group);
            new ConsumerHttpServer(group).getConsumerChannle(20000);
            LOGGER.info("start consumer");
        }


    }
    //客户端初始化
    public static void clientInit(IRegistry registry,EventLoopGroup group) throws Exception {
        List<Endpoint> endpoints = registry.find("com.alibaba.dubbo.performance.demo.provider.IHelloService");
        ListIterator<Endpoint> it = endpoints.listIterator();
        while (it.hasNext()) {
            Endpoint temp = it.next();
            if (temp.getSize() == 2) {
                it.add(temp);
            }
        }
        pool.putClientPool(endpoints, group);
    }

    //服务端初始化
    public static void serverInit(EventLoopGroup group){
        pool.putServerPool( group);
    }


}
