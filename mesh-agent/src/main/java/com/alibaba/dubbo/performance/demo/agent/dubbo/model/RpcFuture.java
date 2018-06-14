package com.alibaba.dubbo.performance.demo.agent.dubbo.model;

import io.netty.buffer.ByteBuf;

import java.util.function.Consumer;

public class RpcFuture {

    private Consumer<ByteBuf> sender;

    public RpcFuture(Consumer consumer){
        this.sender = consumer;
    }


    public void done(ByteBuf response){
        sender.accept(response);
    }
}
