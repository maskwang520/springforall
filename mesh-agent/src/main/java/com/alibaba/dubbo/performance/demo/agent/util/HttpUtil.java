package com.alibaba.dubbo.performance.demo.agent.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

public class HttpUtil {

    public static void Ok(DeferredResult<ResponseEntity> result, Object data){
        result.setResult(new ResponseEntity(data, HttpStatus.OK));
    }

    public static void Error(DeferredResult<ResponseEntity> result, Object data){
        result.setResult(new ResponseEntity(data, HttpStatus.INTERNAL_SERVER_ERROR));
    }

}
