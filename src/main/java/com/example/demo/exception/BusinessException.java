package com.example.demo.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessException extends RuntimeException {
    private String code;
    private Object[] args;

    public BusinessException(String code, Object... args) {
        super();
        this.code = code;
        this.args = args;
    }
}