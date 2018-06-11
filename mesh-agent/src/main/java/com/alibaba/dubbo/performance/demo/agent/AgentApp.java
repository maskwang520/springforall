package com.alibaba.dubbo.performance.demo.agent;

import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.modle.RegistrySingleton;
import com.alibaba.dubbo.performance.demo.agent.registry.IRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * 程序入口类
 */
@SpringBootApplication
public class AgentApp {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentApp.class);
    // agent会作为sidecar，部署在每一个Provider和Consumer机器上
    // 在Provider端启动agent时，添加JVM参数-Dtype=provider -Dserver.port=30000 -Ddubbo.protocol.port=20889
    // 在Consumer端启动agent时，添加JVM参数-Dtype=consumer -Dserver.port=20000
    // 添加日志保存目录: -Dlogs.dir=/path/to/your/logs/dir。请安装自己的环境来设置日志目录。

    public static void main(String[] args) {

        String type = System.getProperty("type");
        IRegistry registry = RegistrySingleton.getInstance();
        if ("provider".equals(type)) {
            LOGGER.info("start provider");
            new ServerConnector().connect(Integer.valueOf(System.getProperty("server.port")));
        }else {
            //SpringApplication.run(AgentApp.class, args);
            try {
                new ConsumerHttpServer().getConsumerChannle(Integer.valueOf(System.getProperty("server.port")));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
