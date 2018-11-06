package com.ttn.elasticsearchAPI.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@Data
@AllArgsConstructor
@ToString
public class SearchDTO {

    private String query;

    private String path;

    private String requestMethod;

    private String responseFilters;

    private Integer max;

    private Integer offset;

    public Map<String, String> getResponseFilters() {
        return Collections.unmodifiableMap(new HashMap<String, String>() {
            {
                put("filter_path", responseFilters);
                put("pretty", "true");
            }
        });
    }

}
