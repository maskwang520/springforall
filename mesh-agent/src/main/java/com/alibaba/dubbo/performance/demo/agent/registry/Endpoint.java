package com.alibaba.dubbo.performance.demo.agent.registry;

public class Endpoint {
    private final String host;
    private final int port;
    private final int size;

    public Endpoint(String host,int port, int size){
        this.host = host;
        this.port = port;
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String toString(){
        return host + ":" + port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Endpoint)) return false;

        Endpoint endpoint = (Endpoint) o;

        if (port != endpoint.port) return false;
        if (size != endpoint.size) return false;
        return host != null ? host.equals(endpoint.host) : endpoint.host == null;
    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + port;
        result = 31 * result + size;
        return result;
    }
}
