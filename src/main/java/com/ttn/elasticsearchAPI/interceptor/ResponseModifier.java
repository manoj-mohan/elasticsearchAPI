package com.ttn.elasticsearchAPI.interceptor;

import com.ttn.elasticsearchAPI.dto.ResponseDTO;
import com.ttn.elasticsearchAPI.util.ConfigHelper;
import groovy.lang.Closure;
import groovy.util.ConfigObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class ResponseModifier implements ResponseBodyAdvice<Object> {

    private final ConfigHelper configHelper;

    @Autowired
    public ResponseModifier(ConfigHelper configHelper) {
        this.configHelper = configHelper;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (configHelper.isConfiguredRoute() && (body.getClass() == ResponseDTO.class)) {
            ConfigObject processorConfig = configHelper.getProcessorMap();
            if (!(processorConfig.isEmpty() || ((ConfigObject) processorConfig.get("post")).isEmpty())) {
                body = ((Closure) ((ConfigObject) processorConfig.get("post")).get("json")).call(body, returnType, selectedContentType, selectedConverterType, request, response);
            }
        }
        return body;
    }
}
