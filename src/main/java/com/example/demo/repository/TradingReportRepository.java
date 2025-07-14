package com.example.demo.repository;

import com.example.demo.entity.TradingReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradingReportRepository extends JpaRepository<TradingReport, Long> {
}
