package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "trading_report_odd")
public class TradingReportOdd {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "main_volume")
    private String mainVolume;

    @Column(name = "main_value")
    private String mainValue;

    @Column(name = "big_lot_volume")
    private String bigLotVolume;

    @Column(name = "big_lot_value")
    private String bigLotValue;

    @Column(name = "buying_volume")
    private String buyingVolume;

    @Column(name = "buying_order")
    private String buyingOrder;

    @Column(name = "selling_volume")
    private String sellingVolume;

    @Column(name = "selling_order")
    private String sellingOrder;

    @Column(name = "total_volume")
    private String totalVolume;

    @Column(name = "total_value")
    private String totalValue;

    @Column(name = "record_date")
    private LocalDate recordDate;

    @Column(name = "created_date")
    private LocalDateTime createdDate;
}
