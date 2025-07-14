package com.example.demo.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessException extends RuntimeException {
    private String code;
    private String message;
    private Object[] args;

    public BusinessException(String code, String message, Object... args) {
        super();
        this.code = code;
        this.message = message;
        this.args = args;
    }
}