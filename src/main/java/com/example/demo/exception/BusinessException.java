package com.example.demo.exception;

import lombok.Data;

@Data
public class BusinessException extends RuntimeException {
    private String code;
    private String message;
    private Object data;

    public BusinessException(String code, String message, Object data) {
        super();
        this.code = code;
        this.message = message;
        this.data = data;
    }
}