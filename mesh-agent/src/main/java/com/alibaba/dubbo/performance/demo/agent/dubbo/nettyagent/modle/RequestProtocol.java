package com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.modle;

import java.io.Serializable;

/**
 * Created by maskwang on 18-6-5.
 */
public class RequestProtocol implements Serializable {


    int requestId;

    int interfaceLen;
    String interfaceName;

    int methodLen;
    String methodName;

    int parameterTypeLen;
    String parameterType;

    int paraLen;
    String param;

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }
}
