package com.alibaba.dubbo.performance.demo.agent.netty.model;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class NettyRequestHolder {

    // key: requestId     value: RpcFuture
    private static ConcurrentHashMap<Integer, Consumer> processingRequest = new ConcurrentHashMap<>();

    public static void put(Integer requestId, Consumer rpcFuture) {
        processingRequest.put(requestId, rpcFuture);
    }

    public static Consumer get(Integer requestId) {
        return processingRequest.get(requestId);
    }

    public static void remove(Integer requestId) {
        processingRequest.remove(requestId);
    }
}

