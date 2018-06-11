package com.alibaba.dubbo.performance.demo.agent.util;

import io.netty.channel.pool.SimpleChannelPool;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by maskwang on 18-6-11.
 */
public class ChannelPoolMap {

    static ConcurrentHashMap<String,SimpleChannelPool> concurrentHashMap = new ConcurrentHashMap();

    public static void put(String addr,SimpleChannelPool scp){
        concurrentHashMap.put(addr,scp);
    }

    public static boolean contain(String addr){

        return concurrentHashMap.containsKey(addr);
    }

    public static SimpleChannelPool get(String addr){
        return concurrentHashMap.get(addr);
    }
}
