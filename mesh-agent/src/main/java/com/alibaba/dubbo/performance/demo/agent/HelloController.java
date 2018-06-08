package com.alibaba.dubbo.performance.demo.agent;

import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClient;
import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.demo.agent.registry.IRegistry;
import com.alibaba.dubbo.performance.demo.agent.util.HttpUtil;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Random;

@RestController
public class HelloController {

    private Logger logger = LoggerFactory.getLogger(HelloController.class);


    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private AsyncHttpClient asyncHttpClient = org.asynchttpclient.Dsl.asyncHttpClient();

    private Random random = new Random();
    private List<Endpoint> endpoints = null;
    private Object lock = new Object();
//    private OkHttpClient httpClient = new OkHttpClient();


    @RequestMapping(value = "")
    public DeferredResult<ResponseEntity> invoke(@RequestParam("interface") String interfaceName,
                                                 @RequestParam("method") String method,
                                                 @RequestParam("parameterTypesString") String parameterTypesString,
                                                 @RequestParam("parameter") String parameter, HttpServletResponse response) throws Exception {
        String type = System.getProperty("type");   // 获取type参数
        DeferredResult<ResponseEntity> deferredResult = new DeferredResult<>();
        if ("consumer".equals(type)) {
            consumer(interfaceName, method, parameterTypesString, parameter, deferredResult);
        } else if ("provider".equals(type)) {
            provider(interfaceName, method, parameterTypesString, parameter, deferredResult);
        } else {
            HttpUtil.Ok(deferredResult, "Environment variable type is needed to set to provider or consumer.");
        }
        return deferredResult;
    }

    public void provider(String interfaceName, String method, String parameterTypesString, String parameter, DeferredResult<ResponseEntity> deferredResult) throws Exception {
        rpcClient.invoke(interfaceName, method, parameterTypesString, parameter, deferredResult);
    }

    public void consumer(String interfaceName, String method, String parameterTypesString, String parameter, DeferredResult<ResponseEntity> deferredResult) throws Exception {

        if (null == endpoints) {
            synchronized (lock) {
                if (null == endpoints) {
                    endpoints = registry.find("com.alibaba.dubbo.performance.demo.provider.IHelloService");
                }
            }
        }

        // 简单的负载均衡，随机取一个
        Endpoint endpoint = endpoints.get(random.nextInt(endpoints.size()));

        String url = "http://" + endpoint.getHost() + ":" + endpoint.getPort();

        org.asynchttpclient.Request r = org.asynchttpclient.Dsl.post(url)
                .addFormParam("interface", interfaceName)
                .addFormParam("method", method)
                .addFormParam("parameterTypesString", parameterTypesString)
                .addFormParam("parameter", parameter)
                .build();

        ListenableFuture<org.asynchttpclient.Response> responseFuture = asyncHttpClient.executeRequest(r);
        responseFuture.addListener(() -> {
            try {
//                logger.info(Thread.currentThread().getName());
                HttpUtil.Ok(deferredResult, responseFuture.get().getResponseBody().trim());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, threadPoolTaskExecutor);
//        logger.info(Thread.currentThread().getName());

//        CompletableFuture.supplyAsync(() -> {
//            RequestBody requestBody = new FormBody.Builder()
//                    .add("interface", interfaceName)
//                    .add("method", method)
//                    .add("parameterTypesString", parameterTypesString)
//                    .add("parameter", parameter)
//                    .build();
//
//            Request request = new Request.Builder()
//                    .url(url)
//                    .post(requestBody)
//                    .build();
//            logger.info("thread-name: " + Thread.currentThread().getName());
//            try (Response response = httpClient.newCall(request).execute()) {
//                byte[] bytes = response.body().bytes();
//                String s = new String(bytes).trim();
//                return Integer.valueOf(s);
//            } catch (IOException e) {
//                e.printStackTrace();
//                return 0;
//            }
//        }).whenCompleteAsync((result, throwable) -> HttpUtil.Ok(deferredResult, result));
    }
}
