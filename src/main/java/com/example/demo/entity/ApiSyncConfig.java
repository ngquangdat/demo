package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "api_sync_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiSyncConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api_url", nullable = false)
    private String apiUrl;

    @Column(name = "target_table", nullable = false)
    private String targetTable;

    @Column(name = "record_json_path", nullable = false)
    private String recordJsonPath;

    @Column(name = "page_response_path")
    private String pageResponsePath;

    @Column(name = "total_page_response_path")
    private String totalPageResponsePath;

    @Column(name = "total_count_response_path")
    private String totalCountResponsePath;

    @Column(name = "last_synced")
    private LocalDateTime lastSynced;

    @Column(name = "sync_code")
    private String syncCode;

    @Column(name = "status")
    private String status;

    @Column(name = "http_method", nullable = false)
    private String httpMethod = "GET";

    @Column(name = "clean_condition")
    private String cleanCondition;

    @Column(name = "upsert_key")
    private String upsertKey;

    @Column(name = "parallel")
    private String parallel;

    @Column(name = "cron")
    private String cron;

    @ToString.Exclude
    @OneToMany(mappedBy = "apiSyncConfig", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ApiSyncFieldMapping> fieldMappings;

    @ToString.Exclude
    @OneToMany(mappedBy = "apiSyncConfig", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ApiSyncParamConfig> paramConfigs;


} 