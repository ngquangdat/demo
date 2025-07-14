package com.example.demo.factory.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseStatus {
    private String code;
    private String message;
    private String traceId;
}
