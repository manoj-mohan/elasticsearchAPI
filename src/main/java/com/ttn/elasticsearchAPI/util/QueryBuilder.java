package com.ttn.elasticsearchAPI.util;


import com.ttn.elasticsearchAPI.co.SearchCO;
import lombok.AllArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@CommonsLog
@Component
@AllArgsConstructor
public class QueryBuilder {

    @Autowired
    private ConfigHelper configHelper;

    public String generateSearchQuery(SearchCO searchCO) {
        log.debug("-> generateSearchQuery");
        log.debug("-> configHelper.getSearchQuery()");
        String query = configHelper.getSearchQuery();
        log.debug("<- configHelper.getSearchQuery()");
        query = StringUtils.replace(query, "##SEARCH_QUERY##", searchCO.getQuery());
        query = StringUtils.replace(query, "##MAX##", searchCO.getLimit().toString());
        query = StringUtils.replace(query, "##OFFSET##", searchCO.getOffset().toString());
        log.debug("<- generateSearchQuery");
        return query;
    }
}