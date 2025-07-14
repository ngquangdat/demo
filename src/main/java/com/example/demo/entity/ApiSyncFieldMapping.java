package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "api_sync_field_mapping")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiSyncFieldMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "config_id", nullable = false)
    private ApiSyncConfig apiSyncConfig;

    @Column(name = "json_path")
    private String jsonPath;

    @Column(name = "target_column", nullable = false)
    private String targetColumn;

    @Column(name = "default_value")
    private String defaultValue;

    @Column(name = "operation")
    private String operation;


} 