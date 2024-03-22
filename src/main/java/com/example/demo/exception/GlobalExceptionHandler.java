//package com.example.demo.exception;
//
//import com.example.demo.constant.ResponseStatusConstant;
//import com.example.demo.factory.response.GeneralResponse;
//import com.example.demo.factory.response.ResponseFactory;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
//
//@Slf4j
//@ControllerAdvice
//public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
//    @ExceptionHandler(value = Exception.class)
//    public ResponseEntity<Object> handleException(Exception e) {
//        GeneralResponse<Object> response;
//        if (e instanceof BusinessException businessException) {
//            response = ResponseFactory.fail(businessException.getCode(), businessException.getData());
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        }
//        log.error(e.getMessage(), e);
//        response = ResponseFactory.fail(ResponseStatusConstant.INTERNAL_SERVER_ERROR, null);
//        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//}
