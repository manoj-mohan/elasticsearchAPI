package com.ttn.elasticsearchAPI.interceptor;


import com.ttn.elasticsearchAPI.util.ConfigHelper;
import groovy.util.ConfigObject;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import groovy.lang.Closure;

@CommonsLog
@Component
public class RequestInterceptor implements HandlerInterceptor {

    private final ConfigHelper configHelper;

    @Autowired
    public RequestInterceptor(ConfigHelper configHelper) {
        this.configHelper = configHelper;
    } 

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean validPreHandling = false;
        ConfigObject processorConfig = configHelper.getProcessorMap();
        if (!processorConfig.isEmpty() && !((ConfigObject) processorConfig.get("pre")).isEmpty()) {
            Object processResponse = ((Closure) ((ConfigObject) processorConfig.get("pre")).get("json")).call(request, response, handler);    
            if (processResponse instanceof Boolean) {
                validPreHandling = (boolean) processResponse;
            }
        }
        return validPreHandling;
    }
}
