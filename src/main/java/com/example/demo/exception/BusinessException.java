package com.example.demo.exception;

import lombok.Data;

@Data
public class BusinessException extends RuntimeException {
    private String code;
    private String description;
    private Object data;

    public BusinessException(String code, String description, Object data) {
        super();
        this.code = code;
        this.description = description;
        this.data = data;
    }
}