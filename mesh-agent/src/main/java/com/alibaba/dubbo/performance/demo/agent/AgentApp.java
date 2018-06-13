package com.alibaba.dubbo.performance.demo.agent;

import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.modle.RegistrySingleton;
import com.alibaba.dubbo.performance.demo.agent.registry.IRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 程序入口类
 */

public class AgentApp {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentApp.class);

    public static void main(String[] args) throws Exception {

        String type = System.getProperty("type");
        IRegistry registry = RegistrySingleton.getInstance();
        if ("provider".equals(type)) {
            LOGGER.info("start provider");
            new ServerConnector().connect(19980);
        } else {
            new ConsumerHttpServer().getConsumerChannle(Integer.valueOf(20000));

        }


    }
}
