package com.alibaba.dubbo.performance.demo.agent.dubbo.model;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class RpcFuture implements Future<Object> {
    private CountDownLatch latch = new CountDownLatch(1);

    private RpcResponse response;
    private Consumer<String> sender;

    public RpcFuture(Consumer<String> sender) {
        this.sender = sender;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public Object get() throws InterruptedException {
         //boolean b = latch.await(100, TimeUnit.MICROSECONDS);
        latch.await();
        try {
            return response.getBytes();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "Error";
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException {
        boolean b = latch.await(timeout,unit);
        return response.getBytes();
    }

    public void done(RpcResponse response){
        this.response = response;
        sender.accept(new String(response.getBytes()).trim());
    }
}
