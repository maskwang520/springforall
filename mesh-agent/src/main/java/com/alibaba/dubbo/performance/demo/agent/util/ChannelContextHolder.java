package com.alibaba.dubbo.performance.demo.agent.util;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;

/**
 * Created by maskwang on 18-6-9.
 */
public class ChannelContextHolder {

    private static HashMap<Integer,ChannelHandlerContext> channelMap = new HashMap();

    public static ChannelHandlerContext getChannelContext(Integer requestId){
        return channelMap.get(requestId);
    }

    public  static void putChannelContext(Integer requestId,ChannelHandlerContext channel){
        channelMap.put(requestId,channel);
    }

    public static void removeChannelContext(Integer requestId){
        channelMap.remove(requestId);
    }
}
