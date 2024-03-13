package com.example.demo.exception;

import lombok.Data;

@Data
public class BusinessException extends RuntimeException {
    private String code;
    private Object data;

    public BusinessException(String code, Object data) {
        super();
        this.code = code;
        this.data = data;
    }
}