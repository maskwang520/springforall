package com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.modle;

import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;

/**
 * Created by maskwang on 18-6-6.
 * 静态内部类实现单例
 */
public class RegistrySingleton {

    private RegistrySingleton(){

    }

    private static class HolderClass {
        private final static EtcdRegistry instance = new EtcdRegistry(System.getProperty("etcd.url"));
    }

    public static EtcdRegistry getInstance() {
        return HolderClass.instance;
    }
}
