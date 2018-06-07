package com.alibaba.dubbo.performance.demo.agent;

import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClient;
import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.demo.agent.registry.IRegistry;
import com.alibaba.dubbo.performance.demo.agent.util.HttpUtil;
import okhttp3.*;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Random;

@RestController
public class HelloController {

    private Logger logger = LoggerFactory.getLogger(HelloController.class);

    private IRegistry registry = new EtcdRegistry(System.getProperty("etcd.url"));

    private AsyncHttpClient asyncHttpClient = org.asynchttpclient.Dsl.asyncHttpClient();
    private RpcClient rpcClient = new RpcClient(registry);
    private Random random = new Random();
    private List<Endpoint> endpoints = null;
    private Object lock = new Object();
    private OkHttpClient httpClient = new OkHttpClient();


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
            HttpUtil.Ok(deferredResult,"Environment variable type is needed to set to provider or consumer.");
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

        ListenableFuture<org.asynchttpclient.Response> responseFuture = asyncHttpClient.executeRequest(r, new AsyncCompletionHandler<Response>() {
            @Override
            public Response onCompleted(Response response) throws Exception {
                HttpUtil.Ok(deferredResult, response.getResponseBody().trim());
                return response;
            }
        });
    }
}
