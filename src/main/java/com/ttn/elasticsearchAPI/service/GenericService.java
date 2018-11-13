package com.ttn.elasticsearchAPI.service;

import com.ttn.elasticsearchAPI.dto.ResponseDTO;
import com.ttn.elasticsearchAPI.dto.SearchDTO;
import com.ttn.elasticsearchAPI.util.ConfigHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@Slf4j
@Component
public class GenericService {

    private RestClient restClient;

    private ConfigHelper configHelper;

    @Autowired
    public GenericService(ConfigHelper configHelper) {
        this.configHelper = configHelper;
        initializeConnection();
    }

    private void initializeConnection() {
        if (restClient == null) {
            RestClientBuilder builder = RestClient
                    .builder(new HttpHost(
                            configHelper.getElasticsearchHost(),
                            configHelper.getElasticsearchPort())
                    ).setRequestConfigCallback(requestConfigBuilder ->
                            requestConfigBuilder.setConnectTimeout(configHelper.getElasticsearchConnectionTimeout())
                                    .setSocketTimeout(configHelper.getElasticsearchSocketTimeout()))
                    .setMaxRetryTimeoutMillis(configHelper.getElasticsearchRetryTimeout());
            restClient = builder.build();
        }
    }

    public ResponseDTO search(SearchDTO dto) throws IOException {
        log.debug("-> search");
        log.trace(dto.toString());
        HttpEntity entity = new NStringEntity(dto.getQuery(), ContentType.APPLICATION_JSON);
        Response response = restClient.performRequest(
                dto.getRequestMethod(),
                dto.getPath(),
                dto.getResponseFilters(),       
                entity
        );
        log.debug("<- search");
        return new ResponseDTO(response, dto);
    }

    @PreDestroy
    void shutdown() throws IOException {
        restClient.close();
    }

}
