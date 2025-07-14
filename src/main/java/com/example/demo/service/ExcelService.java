package com.example.demo.service;

import com.example.demo.dto.SyncDataEvent;
import com.example.demo.entity.TradingReport;
import com.example.demo.repository.TradingReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelService {

    private final TradingReportRepository tradingReportRepository;

    @SneakyThrows
    public void genTradingReport() {
        List<TradingReport> reports = tradingReportRepository.findAll();
        // Excel file path
        String excelFilePath = "TradingReport.xlsx";

        Workbook workbook = new XSSFWorkbook();

        // Create a new sheet
        Sheet sheet = workbook.createSheet("Trading Report");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] columns = {
                "ID", "Main Volume", "Main Value", "Big Lot Volume", "Big Lot Value",
                "Buying Volume", "Buying Order", "Selling Volume", "Selling Order",
                "Total Volume", "Total Value", "Record Date"
        };

        // Style for header
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Populate header row
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        // Date formatter for timestamp
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Populate data rows
        int rowNum = 1;
        for (TradingReport report : reports) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(report.getId());
            row.createCell(1).setCellValue(report.getMainVolume() != null ? report.getMainVolume() : "");
            row.createCell(2).setCellValue(report.getMainValue() != null ? report.getMainValue() : "");
            row.createCell(3).setCellValue(report.getBigLotVolume() != null ? report.getBigLotVolume() : "");
            row.createCell(4).setCellValue(report.getBigLotValue() != null ? report.getBigLotValue() : "");
            row.createCell(5).setCellValue(report.getBuyingVolume() != null ? report.getBuyingVolume() : "");
            row.createCell(6).setCellValue(report.getBuyingOrder() != null ? report.getBuyingOrder() : "");
            row.createCell(7).setCellValue(report.getSellingVolume() != null ? report.getSellingVolume() : "");
            row.createCell(8).setCellValue(report.getSellingOrder() != null ? report.getSellingOrder() : "");
            row.createCell(9).setCellValue(report.getTotalVolume() != null ? report.getTotalVolume() : "");
            row.createCell(10).setCellValue(report.getTotalValue() != null ? report.getTotalValue() : "");
            row.createCell(11).setCellValue(
                    report.getRecordDate() != null
                            ? report.getRecordDate().format(dateFormatter)
                            : ""
            );
        }

        // Auto-size columns
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write to Excel file
        try (FileOutputStream fileOut = new FileOutputStream(excelFilePath)) {
            workbook.write(fileOut);
            log.info("Excel file created successfully: " + excelFilePath);
        }
    }

    @EventListener
    public void processSyncDataEvent(SyncDataEvent syncDataEvent) {
        if ("TRADING_REPORT".equals(syncDataEvent.getSyncCode())) {
            genTradingReport();
        }
    }
}