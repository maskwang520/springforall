package com.alibaba.dubbo.performance.demo.agent.registry;

import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClient;

public class RegistryInstance {
    private static class Instance{
        private static IRegistry registry = new EtcdRegistry(System.getProperty("etcd.url"));
    }

    private RegistryInstance() {

    }

    public static IRegistry getInstance() {
        return Instance.registry;
    }
}
