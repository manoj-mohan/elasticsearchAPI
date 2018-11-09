package com.ttn.elasticsearchAPI.util;


import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;


@Primary
@Component
public class ConfigHelper {

    private ConfigObject configObject;

    private Map<String, Map<String, ConfigObject>> apiConfigMap;

    private ConfigHelper() throws MalformedURLException {
        configObject = new ConfigSlurper().parse(new File(System.getenv("SEARCH_CONFIG_PATH")).toURI().toURL());
        apiConfigMap = buildAPIConfigMap();
    }

    private ConfigObject getElasticSearchConfig() {
        return (ConfigObject) configObject.get("elasticsearch");
    }

    private ConfigObject getElasticSearchTimeoutConfig() {
        return (ConfigObject) getElasticSearchConfig().get("timeouts");
    }

    private ConfigObject getGETAPIConfig() {
        return (ConfigObject) getAPIConfig().get("get");
    }

    private ConfigObject getPOSTAPIConfig() {
        return (ConfigObject) getAPIConfig().get("post");
    }

    private ConfigObject getAPIConfigForCurrentRequest() {
        return apiConfigMap
                .get(getCurrentRequest().getMethod())
                .get(getCurrentRequest().getRequestURI());
    }

    private HashMap<String, Map<String, ConfigObject>> buildAPIConfigMap() {
        HashMap<String, Map<String, ConfigObject>> configMap = new HashMap<>();
        HashMap<String, ConfigObject> getConfig = new HashMap<>();
        HashMap<String, ConfigObject> postConfig = new HashMap<>();
        getPOSTAPIConfig().values().forEach(configObject -> postConfig.put((String) ((ConfigObject) configObject).get("uri"), (ConfigObject) configObject));
        getGETAPIConfig().values().forEach(configObject -> getConfig.put((String) ((ConfigObject) configObject).get("uri"), (ConfigObject) configObject));
        configMap.put(RequestMethod.POST.name(), postConfig);
        configMap.put(RequestMethod.GET.name(), getConfig);
        return configMap;
    }

    public HttpServletRequest getCurrentRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            return request;
        }
        return null;
    }

    public ConfigObject getAPIConfig() {
        return (ConfigObject) configObject.get("api");
    }

    public String getElasticsearchHost() {
        return (String) getElasticSearchConfig().get("url");
    }

    public Integer getElasticsearchPort() {
        return (Integer) getElasticSearchConfig().get("port");
    }

    public Integer getElasticsearchConnectionTimeout() {
        return (Integer) getElasticSearchTimeoutConfig().get("connect");
    }

    public Integer getElasticsearchSocketTimeout() {
        return (Integer) getElasticSearchTimeoutConfig().get("socket");
    }

    public Integer getElasticsearchRetryTimeout() {
        return (Integer) getElasticSearchTimeoutConfig().get("retry");
    }

    public String getSearchIndexPath() {
        ConfigObject configObject = getAPIConfigForCurrentRequest();
        return (String) ((ConfigObject) configObject.get("operation")).get("path");
    }

    public String getSearchQuery() {
        ConfigObject configObject = getAPIConfigForCurrentRequest();
        return (String) ((ConfigObject) configObject.get("operation")).get("query");
    }

    public String getResponseFilters() {
        ConfigObject configObject = getAPIConfigForCurrentRequest();

        return (String) ((ConfigObject) configObject.get("operation")).get("responseFilters");
    }

    public ConfigObject getProcessorMap() {
        ConfigObject configObject = getAPIConfigForCurrentRequest();
        return ((ConfigObject) configObject.get("processors"));
    }

    public Boolean isConfiguredRoute() {
        return getAPIConfigForCurrentRequest() != null;
    }
}
