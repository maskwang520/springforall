package com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.connectionpool;

import io.netty.channel.Channel;
import io.netty.channel.EventLoop;

import java.util.concurrent.ConcurrentHashMap;

public class EventLoopMap {

    public static ConcurrentHashMap<EventLoop,Channel> map = new ConcurrentHashMap();

    public void put (EventLoop loop,Channel channel){
        map.put(loop,channel);
    }

    public Channel get(EventLoop loop){
        return map.get(loop);
    }

}
