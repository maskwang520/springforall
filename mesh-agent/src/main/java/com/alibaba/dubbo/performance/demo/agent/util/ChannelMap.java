package com.alibaba.dubbo.performance.demo.agent.util;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by maskwang on 18-6-12.
 */
public class ChannelMap {

    public static ConcurrentHashMap<String,Channel> map = new ConcurrentHashMap();

    public void put(String hashcode,Channel channel){
        map.put(hashcode,channel);
    }

    public void remove(String hashcode){
        map.remove(hashcode);
    }

    public Channel get(String hashcode){
        return map.get(hashcode);
    }

}
