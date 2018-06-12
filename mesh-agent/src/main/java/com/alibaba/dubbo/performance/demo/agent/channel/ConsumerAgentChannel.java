package com.alibaba.dubbo.performance.demo.agent.channel;

import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.connectionpool.NettyPoolClient;
import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.modle.RegistrySingleton;
import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.registry.IRegistry;
import com.alibaba.dubbo.performance.demo.agent.util.ChannelPoolMap;
import io.netty.channel.Channel;
import io.netty.channel.pool.SimpleChannelPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

/**
 * Created by maskwang on 18-6-9.
 */
public class ConsumerAgentChannel {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerAgentChannel.class);

    private static Random random = new Random();
    private static List<Endpoint> endpoints = null;
    private static NettyPoolClient client;

    public static Channel getChannel(NettyPoolClient client1) throws Exception {
        client = client1;
        IRegistry registry = RegistrySingleton.getInstance();

        if (null == endpoints) {
            synchronized (ConsumerAgentChannel.class) {
                if (null == endpoints) {
                    endpoints = registry.find("com.alibaba.dubbo.performance.demo.provider.IHelloService");
                    LOGGER.info("endpoint's size is{}", endpoints.size());
                }
            }
        }

        Endpoint endpoint = endpoints.get(random.nextInt(endpoints.size()));
        //LOGGER.info("select:",endpoint.getHost(),endpoint.getPort());
        InetSocketAddress addr = new InetSocketAddress(endpoint.getHost(), endpoint.getPort());
        SimpleChannelPool pool = client.poolMap.get(addr);
        Channel channel = pool.acquire().get();
        //ChannelPoolMap.contain(channel.remoteAddress().toString())
        ChannelPoolMap.put(channel.remoteAddress().toString(), pool);

        return channel;
    }
}
