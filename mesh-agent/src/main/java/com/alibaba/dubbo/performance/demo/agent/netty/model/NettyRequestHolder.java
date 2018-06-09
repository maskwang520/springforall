package com.alibaba.dubbo.performance.demo.agent.netty.model;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcFuture;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

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

