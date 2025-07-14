package com.example.demo.repository;

import com.example.demo.entity.ApiSyncParamConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiSyncParamConfigRepository extends JpaRepository<ApiSyncParamConfig, Long> {

}