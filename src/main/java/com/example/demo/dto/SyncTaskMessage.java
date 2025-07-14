package com.example.demo.dto;

import com.example.demo.entity.ApiSyncConfig;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SyncTaskMessage {
    private String syncCode;
    private LocalDateTime startTime;
    private int page;
    private int size;
    private ApiSyncConfig config;
}
