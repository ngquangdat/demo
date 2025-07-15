package com.example.demo.repository;

import com.example.demo.entity.TradingReportOdd;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TradingReportOddRepository extends JpaRepository<TradingReportOdd, Long> {

    List<TradingReportOdd> findFirstByRecordDateOrderByCreatedDateDesc(LocalDate recordDate);

}
