package com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.modle;

import java.io.Serializable;

/**
 * Created by maskwang on 18-6-5.
 */
public class RequestProtocol implements Serializable {


    int requestId;

    byte[] content;


    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
