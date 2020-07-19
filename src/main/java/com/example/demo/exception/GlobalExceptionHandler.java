package com.example.demo.exception;

import com.example.demo.factory.response.GeneralResponse;
import com.example.demo.factory.response.ResponseFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity handleException(Exception e) {
        if(e instanceof BusinessException){
            BusinessException businessException = (BusinessException) e;
            GeneralResponse response = ResponseFactory.fail(businessException.getCode(), businessException.getData());
            response.setData(businessException.getData());
            return new ResponseEntity(response, HttpStatus.OK);
        }
        log.error(e.getMessage(), e);
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
