package com.alibaba.dubbo.performance.demo.agent.registry;

public class Endpoint {
    private final String host;
    private final int port;
    private int size;

    public Endpoint(String host,int port,int size){
        this.host = host;
        this.port = port;
        this.size = size;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getSize() {
        return size;
    }

    public String toString(){
        return host + ":" + port;
    }

    public boolean equals(Object o){
        if (!(o instanceof Endpoint)){
            return false;
        }
        Endpoint other = (Endpoint) o;
        return other.host.equals(this.host) && other.port == this.port;
    }

    public int hashCode(){
        return host.hashCode() + port;
    }
}
