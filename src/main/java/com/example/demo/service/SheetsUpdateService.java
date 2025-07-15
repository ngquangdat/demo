package com.example.demo.service;

import com.example.demo.constant.ResponseCode;
import com.example.demo.dto.SyncDataEvent;
import com.example.demo.entity.TradingReport;
import com.example.demo.entity.TradingReportOdd;
import com.example.demo.exception.BusinessException;
import com.example.demo.repository.TradingReportOddRepository;
import com.example.demo.repository.TradingReportRepository;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SheetsUpdateService {

    private final TradingReportRepository tradingReportRepository;
    private final TradingReportOddRepository tradingReportOddRepository;
    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = "credentials.json";
    private static final DateTimeFormatter FORMATTER1 = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @Value("${spreadsheet-id}")
    private String spreadsheetId;

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        ClassLoader classLoader = SheetsUpdateService.class.getClassLoader();
        InputStream in = classLoader.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    @SneakyThrows
    public void genTradingReport() {
        LocalDate date = LocalDate.now().minusDays(1);
        while (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            date = date.minusDays(1);
        }
        List<TradingReport> reports = tradingReportRepository.findFirstByRecordDateOrderByCreatedDateDesc(date);
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String range = "Sheet1!A1:M10";

        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Data to update in the spreadsheet
        List<List<Object>> values = new ArrayList<>();

        values.add(
                Arrays.asList("ID", "Main Volume", "Main Value", "Big Lot Volume", "Big Lot Value",
                        "Buying Volume", "Buying Order", "Selling Volume", "Selling Order",
                        "Total Volume", "Total Value", "Record Date", "Created Date")
        );
        if (CollectionUtils.isNotEmpty(reports)) {
            for (TradingReport report : reports) {
                List<Object> row = new ArrayList<>();
                row.add(report.getId() != null ? report.getId().toString() : "");
                row.add(report.getMainVolume() != null ? report.getMainVolume() : "");
                row.add(report.getMainValue() != null ? report.getMainValue() : "");
                row.add(report.getBigLotVolume() != null ? report.getBigLotVolume() : "");
                row.add(report.getBigLotValue() != null ? report.getBigLotValue() : "");
                row.add(report.getBuyingVolume() != null ? report.getBuyingVolume() : "");
                row.add(report.getBuyingOrder() != null ? report.getBuyingOrder() : "");
                row.add(report.getSellingVolume() != null ? report.getSellingVolume() : "");
                row.add(report.getSellingOrder() != null ? report.getSellingOrder() : "");
                row.add(report.getTotalVolume() != null ? report.getTotalVolume() : "");
                row.add(report.getTotalValue() != null ? report.getTotalValue() : "");
                row.add(report.getRecordDate() != null ? report.getRecordDate().format(FORMATTER1) : "");
                row.add(report.getCreatedDate() != null ? report.getCreatedDate().format(FORMATTER) : "");
                if (row.stream().filter(""::equals).count() == 10) {
                    values.add(emptyData(date));
                } else {
                    values.add(row);
                }
            }
        } else {
            values.add(emptyData(date));
        }

        ValueRange body = new ValueRange()
                .setValues(values);

        service.spreadsheets().values()
                .update(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();

        // Get the sheet ID for formatting
        Spreadsheet spreadsheet = service.spreadsheets().get(spreadsheetId).execute();
        Sheet sheet = spreadsheet.getSheets().stream()
                .filter(s -> s.getProperties().getTitle().equals("Sheet1"))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ResponseCode.BAD_REQUEST));

        Integer sheetId = sheet.getProperties().getSheetId();
        // Bold the first row (A1:M1)
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest();
        Request formatRequest = new Request().setRepeatCell(new RepeatCellRequest()
                .setRange(new GridRange()
                        .setSheetId(sheetId)
                        .setStartRowIndex(0)
                        .setEndRowIndex(1)
                        .setStartColumnIndex(0)
                        .setEndColumnIndex(13)) // 13 columns (A to M)
                .setCell(new CellData()
                        .setUserEnteredFormat(new CellFormat()
                                .setTextFormat(new TextFormat().setBold(true))))
                .setFields("userEnteredFormat.textFormat.bold"));

        batchUpdateRequest.setRequests(Collections.singletonList(formatRequest));
        service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
        log.info("Spreadsheet updated successfully!");
    }

    @SneakyThrows
    public void genTradingReport2() {
        LocalDate date = LocalDate.now().minusDays(1);
        while (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            date = date.minusDays(1);
        }
        List<TradingReportOdd> reports2 = tradingReportOddRepository.findFirstByRecordDateOrderByCreatedDateDesc(date);
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String range = "Sheet1!A5:M10";

        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Data to update in the spreadsheet
        List<List<Object>> values = new ArrayList<>();

        List<Object> row0 = new ArrayList<>();
        row0.add("THỐNG KÊ GIAO DỊCH LÔ LẺ");
        values.add(row0);

        List<Object> row01 = new ArrayList<>();
        values.add(row01);

        values.add(
                Arrays.asList("ID", "Main Volume", "Main Value", "Big Lot Volume", "Big Lot Value",
                        "Buying Volume", "Buying Order", "Selling Volume", "Selling Order",
                        "Total Volume", "Total Value", "Record Date", "Created Date")
        );
        if (CollectionUtils.isNotEmpty(reports2)) {
            for (TradingReportOdd report : reports2) {
                List<Object> row = new ArrayList<>();
                row.add(report.getId() != null ? report.getId().toString() : "");
                row.add(report.getMainVolume() != null ? report.getMainVolume() : "");
                row.add(report.getMainValue() != null ? report.getMainValue() : "");
                row.add(report.getBigLotVolume() != null ? report.getBigLotVolume() : "");
                row.add(report.getBigLotValue() != null ? report.getBigLotValue() : "");
                row.add(report.getBuyingVolume() != null ? report.getBuyingVolume() : "");
                row.add(report.getBuyingOrder() != null ? report.getBuyingOrder() : "");
                row.add(report.getSellingVolume() != null ? report.getSellingVolume() : "");
                row.add(report.getSellingOrder() != null ? report.getSellingOrder() : "");
                row.add(report.getTotalVolume() != null ? report.getTotalVolume() : "");
                row.add(report.getTotalValue() != null ? report.getTotalValue() : "");
                row.add(report.getRecordDate() != null ? report.getRecordDate().format(FORMATTER1) : "");
                row.add(report.getCreatedDate() != null ? report.getCreatedDate().format(FORMATTER) : "");
                if (row.stream().filter(""::equals).count() == 10) {
                    values.add(emptyData(date));
                } else {
                    values.add(row);
                }
            }
        } else {
            values.add(emptyData(date));
        }


        ValueRange body = new ValueRange()
                .setValues(values);

        service.spreadsheets().values()
                .update(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();

        // Get the sheet ID for formatting
        Spreadsheet spreadsheet = service.spreadsheets().get(spreadsheetId).execute();
        Sheet sheet = spreadsheet.getSheets().stream()
                .filter(s -> s.getProperties().getTitle().equals("Sheet1"))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ResponseCode.BAD_REQUEST));

        Integer sheetId = sheet.getProperties().getSheetId();
        // Bold the first row (A1:M1)
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest();
        Request formatRequest = new Request().setRepeatCell(new RepeatCellRequest()
                .setRange(new GridRange()
                        .setSheetId(sheetId)
                        .setStartRowIndex(6)
                        .setEndRowIndex(7)
                        .setStartColumnIndex(0)
                        .setEndColumnIndex(13)) // 13 columns (A to M)
                .setCell(new CellData()
                        .setUserEnteredFormat(new CellFormat()
                                .setTextFormat(new TextFormat().setBold(true))))
                .setFields("userEnteredFormat.textFormat.bold"));

        batchUpdateRequest.setRequests(Collections.singletonList(formatRequest));
        service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
        log.info("Spreadsheet updated successfully!");
    }

    private List<Object> emptyData(LocalDate date) {
        List<Object> row = new ArrayList<>();
        row.add("Chưa có dữ liệu cho ngày " + date.format(FORMATTER1));
        row.add("");
        row.add("");
        row.add("");
        row.add("");
        row.add("");
        row.add("");
        row.add("");
        row.add("");
        row.add("");
        row.add("");
        row.add("");
        row.add("");
        return row;
    }

    @EventListener
    public void processSyncDataEvent(SyncDataEvent syncDataEvent) {
        if ("TRADING_REPORT".equals(syncDataEvent.getSyncCode())) {
            genTradingReport();
        }
        if ("TRADING_REPORT_ODD".equals(syncDataEvent.getSyncCode())) {
            genTradingReport2();
        }
    }

}