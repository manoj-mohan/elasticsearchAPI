package com.ttn.elasticsearchAPI.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {
    SUCCESS(0, "SUCCESS"),
    ERROR(1, "ERROR"),
    JSON_PARSING_EXCEPTION(2, "JSON_PARSING_EXCEPTION"),
    NO_DATA_FOUND(3, "NO_DATA_FOUND");

    private int ordinal;
    private String message;
}
