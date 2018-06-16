package com.alibaba.dubbo.performance.demo.agent.util;


import io.netty.channel.Channel;
import io.netty.channel.EventLoop;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by maskwang on 18-6-15.
 *
 * 存放EventLoop和channel的对应关系
 */
public class ClientLoopMap {

    static ConcurrentHashMap<EventLoop,List<Channel>> map = new ConcurrentHashMap();

    public void put(EventLoop loop,List<Channel> channels){
        map.put(loop,channels);
    }

    public List<Channel>  get(EventLoop loop){
        return map.get(loop);
    }

    public boolean contains(EventLoop loop){
        return map.containsKey(loop);
    }


}