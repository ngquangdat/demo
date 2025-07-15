package com.example.demo.repository;

import com.example.demo.entity.TradingReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TradingReportRepository extends JpaRepository<TradingReport, Long> {

    List<TradingReport> findFirstByRecordDateOrderByCreatedDateDesc(LocalDate recordDate);

}
