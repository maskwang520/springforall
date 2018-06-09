package com.alibaba.dubbo.performance.demo.agent.netty.model;

import java.util.concurrent.atomic.AtomicInteger;

public class RequestWrapper {
    private static AtomicInteger atomicInt = new AtomicInteger();

    public RequestWrapper(String interfaceName, String method, String parameterTypesString, String parameter) {
        this.requestId = atomicInt.incrementAndGet();
        this.interfaceName = interfaceName;
        this.method = method;
        this.parameterTypesString = parameterTypesString;
        this.parameter = parameter;
    }

    public Integer requestId;
    public String interfaceName;
    public String method;
    public String parameterTypesString;
    public String parameter;

}
