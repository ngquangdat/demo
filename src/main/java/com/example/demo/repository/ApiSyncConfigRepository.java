package com.example.demo.repository;

import com.example.demo.entity.ApiSyncConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiSyncConfigRepository extends JpaRepository<ApiSyncConfig, Long> {

    Optional<ApiSyncConfig> findBySyncCode(String syncCode);

} 