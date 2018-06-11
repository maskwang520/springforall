package com.alibaba.dubbo.performance.demo.agent.registry;

public class RegistryInstance {

    private static IRegistry registry = new EtcdRegistry(System.getProperty("etcd.url"));

    private RegistryInstance() {}

    public static IRegistry getInstance() {
        return registry;
    }

}
