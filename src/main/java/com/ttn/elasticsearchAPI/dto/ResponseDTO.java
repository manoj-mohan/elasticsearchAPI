package com.ttn.elasticsearchAPI.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;

import java.io.IOException;


@Data
@AllArgsConstructor
@ToString
public class ResponseDTO {

    private String searchResponse;

    private StatusLine status;

    private Integer max;

    private Integer offset;

    private String message;

    private String code;

    public ResponseDTO(Response response, SearchDTO dto) throws IOException {
        searchResponse = EntityUtils.toString(response.getEntity());
        status = response.getStatusLine();
        max = dto.getMax();
        offset = dto.getOffset();
    }
}
