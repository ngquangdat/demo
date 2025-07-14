package com.example.demo.exception;

import brave.Tracer;
import com.example.demo.constant.ResponseCode;
import com.example.demo.factory.response.ResponseFactory;
import com.example.demo.factory.response.ResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final Tracer tracer;

    @ExceptionHandler(NestedRuntimeException.class)
    protected ResponseEntity<Object> handleException(final NestedRuntimeException ex) {
        log.warn("An nested exception occurred: {}", ex.getMessage(), ex);

        return ResponseEntity.badRequest().body(
                ResponseStatus.builder()
                        .code(ResponseCode.INTERNAL_SERVER_ERROR)
                        .message(ResponseFactory.getMessage(ResponseCode.INTERNAL_SERVER_ERROR))
                        .traceId(getCurrentTraceId())
                        .build());
    }

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<Object> handleException(BusinessException ex) {
        log.warn("An BusinessException occurred: {} - {}", ex.getCode(), ex.getMessage(), ex);

        return ResponseEntity.badRequest().body(
                ResponseStatus.builder()
                        .code(ex.getCode())
                        .message(ResponseFactory.getMessage(ex.getCode(), ex.getArgs()))
                        .traceId(getCurrentTraceId())
                        .build());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
        log.error("An uncaught exception occurred: {}", e.getMessage(), e);

        return ResponseEntity.internalServerError().body(
                ResponseStatus.builder()
                        .code(ResponseCode.INTERNAL_SERVER_ERROR)
                        .message(ResponseFactory.getMessage(ResponseCode.INTERNAL_SERVER_ERROR))
                        .traceId(getCurrentTraceId())
                        .build());
    }

    private String getCurrentTraceId() {
        if (tracer.currentSpan() != null) {
            return tracer.currentSpan().context().traceIdString();
        }
        return null;
    }

    @Override
    protected ResponseEntity<Object> createResponseEntity(
            @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        if (body instanceof ProblemDetail problemDetail) {
            body = ResponseStatus.builder()
                    .code(ResponseCode.BAD_REQUEST)
                    .message(problemDetail.getDetail())
                    .traceId(getCurrentTraceId())
                    .build();
        }
        return super.createResponseEntity(body, headers, statusCode, request);
    }

}
