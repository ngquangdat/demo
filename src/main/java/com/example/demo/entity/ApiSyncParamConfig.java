package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "api_sync_param_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiSyncParamConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "config_id", nullable = false)
    private ApiSyncConfig apiSyncConfig;

    @Column(name = "param_name", nullable = false)
    private String paramName;

    @Column(name = "param_type")
    private String paramType;

    @Column(name = "default_value")
    private String defaultValue;

    @Column(name = "operation")
    private String operation;


} 