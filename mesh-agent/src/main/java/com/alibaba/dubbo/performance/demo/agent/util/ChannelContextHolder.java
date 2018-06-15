package com.alibaba.dubbo.performance.demo.agent.util;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by maskwang on 18-6-9.
 * 保存和requestId和channle的对应
 */
public class ChannelContextHolder {

    private static ConcurrentHashMap<Integer, ChannelHandlerContext> channelMap = new ConcurrentHashMap<>(64);

    public static ChannelHandlerContext getChannelContext(Integer requestId) {
        return channelMap.get(requestId);
    }

    public static void putChannelContext(Integer requestId, ChannelHandlerContext channel) {
        channelMap.put(requestId, channel);
    }

    public static void removeChannelContext(Integer requestId) {
        channelMap.remove(requestId);
    }
}
