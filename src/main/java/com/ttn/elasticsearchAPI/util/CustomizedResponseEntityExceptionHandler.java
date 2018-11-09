package com.ttn.elasticsearchAPI.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.*;

@ControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        ServletWebRequest servletRequest = (ServletWebRequest) request;
        Map<String, Object> responseData = new LinkedHashMap<>();
        List<String> errors = new ArrayList<>();

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        for (FieldError error : fieldErrors) {
            errors.add(error.getField() + ":" + error.getRejectedValue() + ":" + error.getDefaultMessage());
        }

        responseData.put("timestamp", new Date());
        responseData.put("status", ResponseCode.JSON_PARSING_EXCEPTION.getOrdinal());
        responseData.put("errors", errors);
        responseData.put("message", ResponseCode.JSON_PARSING_EXCEPTION.getMessage());
        responseData.put("path", servletRequest.getRequest().getRequestURI());

        return new ResponseEntity<>(responseData, status);
    }
}