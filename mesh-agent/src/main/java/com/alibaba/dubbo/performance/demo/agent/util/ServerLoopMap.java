package com.alibaba.dubbo.performance.demo.agent.util;

import io.netty.channel.Channel;
import io.netty.channel.EventLoop;

import java.util.concurrent.ConcurrentHashMap;

public class ServerLoopMap {

    private static ConcurrentHashMap<EventLoop,Channel> map = new ConcurrentHashMap<EventLoop, Channel>();

    public void put(EventLoop loop,Channel channel){
        map.put(loop,channel);
    }

    public Channel get(EventLoop loop){
        return map.get(loop);
    }
}
