package com.alibaba.dubbo.performance.demo.agent.netty.model;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcFuture;

import java.util.concurrent.ConcurrentHashMap;

public class NettyRequestHolder {

    // key: requestId     value: RpcFuture
    private static ConcurrentHashMap<String, RpcFuture> processingRequest = new ConcurrentHashMap<>();

    public static void put(String requestId, RpcFuture rpcFuture) {
        processingRequest.put(requestId, rpcFuture);
    }

    public static RpcFuture get(String requestId) {
        return processingRequest.get(requestId);
    }

    public static void remove(String requestId) {
        processingRequest.remove(requestId);
    }
}

