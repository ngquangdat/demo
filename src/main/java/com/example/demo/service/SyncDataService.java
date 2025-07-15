package com.example.demo.service;

import com.example.demo.constant.ResponseCode;
import com.example.demo.dto.SyncDataEvent;
import com.example.demo.dto.SyncTaskMessage;
import com.example.demo.entity.ApiSyncConfig;
import com.example.demo.entity.ApiSyncFieldMapping;
import com.example.demo.entity.ApiSyncParamConfig;
import com.example.demo.exception.BusinessException;
import com.example.demo.repository.ApiSyncConfigRepository;
import com.example.demo.util.TextUtil;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncDataService {

    private final RestTemplate restTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final ApplicationEventPublisher eventPublisher;
    private final ApiSyncConfigRepository apiSyncConfigRepository;
    private final CounterService counterService;


    private static final String PAGE = "PAGE";
    private static final String SIZE = "SIZE";
    private static final String OP_LOCAL_DATE_YYYY_MM_DD = "#LOCAL_DATE_YYYY_MM_DD";
    private static final String LOCAL_DATE_YYYY_MM_DD_PRE_1 = "#LOCAL_DATE_YYYY_MM_DD_PRE_1";
    private static final String LOCAL_DATE_YYYY_MM_DD_PRE_WORK_1 = "#LOCAL_DATE_YYYY_MM_DD_PRE_WORK_1";
    private static final String OP_LOCAL_DATE_TIME = "#LOCAL_DATE_TIME";
    private static final String OP_JSON_PATH_ALL = "#JSON_PATH_ALL";
    private static final String OP_UPPER_CASE = "#UPPER_CASE";
    private static final String OP_LOWER_CASE = "#LOWER_CASE";
    private static final String OP_NUMBER = "#NUMBER";
    private static final String OP_1000_VND = "#1000_VND";


    @Async
    @Transactional
    public void syncData(String syncCode) {
        log.info("syncData {}", syncCode);
        ApiSyncConfig config = apiSyncConfigRepository.findBySyncCode(syncCode)
                .orElseThrow(() -> new BusinessException(ResponseCode.BAD_REQUEST, syncCode));
        List<ApiSyncParamConfig> paramConfig = config.getParamConfigs();
        try {
            counterService.resetCounter(syncCode);
            cleanBeforeSync(config);

            int page = paramConfig.stream()
                    .filter(p -> PAGE.equalsIgnoreCase(p.getParamType()))
                    .map(ApiSyncParamConfig::getDefaultValue)
                    .map(Integer::parseInt)
                    .findFirst()
                    .orElse(0);
            int size = paramConfig.stream()
                    .filter(p -> SIZE.equalsIgnoreCase(p.getParamType()))
                    .map(ApiSyncParamConfig::getDefaultValue)
                    .map(Integer::parseInt)
                    .findFirst()
                    .orElse(100);
            Integer totalPage = 0;

            do {
                SyncTaskMessage task = SyncTaskMessage.builder()
                        .syncCode(syncCode)
                        .page(page)
                        .size(size)
                        .config(config)
                        .build();
                if (page == 0 || !Boolean.TRUE.toString().equalsIgnoreCase(config.getParallel())) {
                    totalPage = processSyncPage(task);

                }
                if (Boolean.TRUE.toString().equalsIgnoreCase(config.getParallel())) {
                    eventPublisher.publishEvent(task);
                }

                page++;
            } while (totalPage != 0 && page <= totalPage);
        } catch (Exception e) {
            log.warn("Error when sync data {}", syncCode);
            config.setStatus("ERROR");
            throw e;
        } finally {
            log.info("Sync data {} done", syncCode);
        }
    }

    public Integer processSyncPage(SyncTaskMessage task) {
        Map<String, String> params = getRequestParams(task.getPage(), task.getSize(), task.getConfig().getParamConfigs());
        String jsonResponse = callApi(task.getConfig().getApiUrl(), HttpMethod.valueOf(task.getConfig().getHttpMethod()), params);
        Object recordRaw = JsonPath.read(jsonResponse, task.getConfig().getRecordJsonPath());
        List<Object> records = new ArrayList<>();
        if (recordRaw instanceof List list) {
            records = list;
        } else {
            records.add(recordRaw);
        }
        if (records.isEmpty()) {
            return 0;
        }
        counterService.incrementCounter(task.getSyncCode(), (long) records.size());
        // Parse data
        List<Map<String, Object>> recordsToDb = parseData(task.getConfig().getFieldMappings(), records, jsonResponse);
        // build và chạy insert sql
        batchUpsertSql(task.getConfig(), recordsToDb);
        if (getTotalCount(task.getConfig(), jsonResponse) == 0
                || getTotalCount(task.getConfig(), jsonResponse) == counterService.getCounterValue(task.getSyncCode())) {
            publishEventSync(task.getSyncCode(), "SUCCESS");
        }
        return getTotalPage(task.getConfig(), jsonResponse);
    }

    @EventListener
    public void handleSyncTask(SyncTaskMessage task) {
        try {
            processSyncPage(task);
        } catch (Exception e) {
            log.error("Failed sync for {}", task, e);
        }
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
    public void processSyncDataEvent(SyncDataEvent syncDataEvent) {
        ApiSyncConfig config = apiSyncConfigRepository.findBySyncCode(syncDataEvent.getSyncCode())
                .orElseThrow(() -> new BusinessException(ResponseCode.BAD_REQUEST, syncDataEvent.getSyncCode()));
        config.setStatus(syncDataEvent.getStatus());
        config.setLastSynced(LocalDateTime.now());
        apiSyncConfigRepository.save(config);
    }

    private void publishEventSync(String syncCode, String status) {
        eventPublisher.publishEvent(
                SyncDataEvent.builder()
                        .syncCode(syncCode)
                        .status(status)
                        .build());
    }

    private List<Map<String, Object>> parseData(List<ApiSyncFieldMapping> mappings,
                                                List<Object> records,
                                                String jsonResponse) {
        List<Map<String, Object>> recordsToDb = new ArrayList<>();
        for (Object item : records) {
            Map<String, Object> valueMap = new HashMap<>();
            for (ApiSyncFieldMapping mapping : mappings) {
                valueMap.put(mapping.getTargetColumn(), getValueInsert(mapping, item, jsonResponse));
            }

            recordsToDb.add(valueMap);
        }
        return recordsToDb;
    }

    private int getTotalPage(ApiSyncConfig config, String jsonResponse) {
        try {
            return JsonPath.parse(jsonResponse).read(config.getTotalPageResponsePath());
        } catch (Exception ignored) {
            log.debug("getTotalPage path not found: {}", config.getTotalPageResponsePath());
        }
        return 0;
    }

    private long getTotalCount(ApiSyncConfig config, String jsonResponse) {
        try {
            return JsonPath.parse(jsonResponse).read(config.getTotalCountResponsePath());
        } catch (Exception ignored) {
            log.debug("getTotalCount path not found: {}", config.getTotalCountResponsePath());
        }
        return 0;
    }

    private Map<String, String> getRequestParams(int page,
                                                 int size,
                                                 List<ApiSyncParamConfig> paramConfig) {

        return paramConfig.stream()
                .collect(Collectors.toMap(
                        ApiSyncParamConfig::getParamName,
                        config -> {
                            if (PAGE.equalsIgnoreCase(config.getParamType())) {
                                return String.valueOf(page);
                            }
                            if (SIZE.equalsIgnoreCase(config.getParamType())) {
                                return String.valueOf(size);
                            }
                            if (OP_LOCAL_DATE_YYYY_MM_DD.equalsIgnoreCase(config.getOperation())) {
                                return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
                            }
                            if (LOCAL_DATE_YYYY_MM_DD_PRE_1.equalsIgnoreCase(config.getOperation())) {
                                return LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
                            }
                            if (LOCAL_DATE_YYYY_MM_DD_PRE_WORK_1.equalsIgnoreCase(config.getOperation())) {
                                return getPreviousWorkDay();
                            }
                            return config.getDefaultValue();
                        }
                ));
    }

    private Object getValueInsert(ApiSyncFieldMapping mapping, Object itemJson, String jsonResponse) {
        Object fieldValue = getFieldValue(mapping, TextUtil.gson.toJson(itemJson));
        if (mapping.getDefaultValue() == null) {
            return transformDefaultValue(mapping, jsonResponse, fieldValue);
        } else {
            return fieldValue;
        }
    }

    private Object getFieldValue(ApiSyncFieldMapping mapping, String itemJson) {
        try {
            if (StringUtils.isBlank(mapping.getJsonPath())) {
                return null;
            }
            Object fieldValue = JsonPath.read(itemJson, mapping.getJsonPath());
            if (fieldValue != null) {
                return (fieldValue instanceof Map<?,?>) ? TextUtil.gson.toJson(fieldValue) : fieldValue;
            }
        } catch (PathNotFoundException ignored) {
            log.debug("JSON path not found: {}", mapping.getJsonPath());
        }
        return null;
    }

    private Object transformDefaultValue(ApiSyncFieldMapping mapping, String jsonResponse, Object fieldValue) {
        if (fieldValue == null) {
            fieldValue = mapping.getDefaultValue();
        }
        if (OP_LOCAL_DATE_TIME.equalsIgnoreCase(mapping.getOperation())) {
            return LocalDateTime.now();
        }
        if (LOCAL_DATE_YYYY_MM_DD_PRE_WORK_1.equalsIgnoreCase(mapping.getOperation())) {
            return getPreviousWorkDay();
        }
        if (OP_JSON_PATH_ALL.equalsIgnoreCase(mapping.getOperation())) {
            return getFieldValue(mapping, jsonResponse);
        }
        if (OP_UPPER_CASE.equalsIgnoreCase(mapping.getOperation())) {
            return (fieldValue instanceof String) ? ((String) fieldValue).toUpperCase() : fieldValue;
        }
        if (OP_LOWER_CASE.equalsIgnoreCase(mapping.getOperation())) {
            return (fieldValue instanceof String) ? ((String) fieldValue).toLowerCase() : fieldValue;
        }
        if (OP_NUMBER.equalsIgnoreCase(mapping.getOperation())) {
            return (fieldValue instanceof String) ? new BigDecimal(fieldValue.toString()) : fieldValue;
        }
        if (OP_1000_VND.equalsIgnoreCase(mapping.getOperation())) {
            if (fieldValue instanceof String val) {
                String cleanNumberStr = val.replace(",", "");
                BigDecimal number = new BigDecimal(cleanNumberStr);
                return number.multiply(new BigDecimal(1000)).stripTrailingZeros();
            }

            return fieldValue;
        }
        return fieldValue;
    }

    private String getPreviousWorkDay() {
        LocalDate date = LocalDate.now().minusDays(1);
        while (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            date = date.minusDays(1);
        }
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public void cleanBeforeSync(ApiSyncConfig config) {
        if (StringUtils.isNotBlank(config.getCleanCondition())) {
            String sql = String.format("DELETE FROM %s WHERE %s",
                    config.getTargetTable(),
                    config.getCleanCondition());
            jdbcTemplate.update(sql);
        }
    }

    private void batchUpsertSql(ApiSyncConfig config, List<Map<String, Object>> records) {
        if (records == null || records.isEmpty()) return;

        List<String> columns = new ArrayList<>(records.getFirst().keySet());
        String columnsStr = String.join(", ", columns);
        String paramsStr = columns.stream().map(k -> "?").collect(Collectors.joining(", "));

        String sql = StringUtils.isBlank(config.getUpsertKey()) ?
                String.format("INSERT INTO %s (%s) VALUES (%s)", config.getTargetTable(), columnsStr, paramsStr) :
                getSqlUpsert(config, columns, columnsStr, paramsStr);

        jdbcTemplate.batchUpdate(sql, records, records.size(), (ps, record) -> {
            for (int i = 0; i < columns.size(); i++) {
                ps.setObject(i + 1, record.get(columns.get(i)));
            }
        });
    }

    private String getSqlUpsert(ApiSyncConfig config, List<String> columns, String columnsStr, String paramsStr) {
        String updateSet = columns.stream()
                .filter(col -> !config.getUpsertKey().contains(col))
                .map(col -> col + " = EXCLUDED." + col)
                .collect(Collectors.joining(", "));
        return String.format("INSERT INTO %s (%s) VALUES (%s) ON CONFLICT (%s) DO UPDATE SET %s",
                config.getTargetTable(),
                columnsStr,
                paramsStr,
                config.getUpsertKey(),
                updateSet);
    }

    private String callApi(String apiUrl,
                           HttpMethod httpMethod,
                           Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiUrl);
        params.forEach(builder::queryParam);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(null, headers);
        String uri = builder.build().toUriString();
        String response = restTemplate.exchange(uri, httpMethod, httpEntity, String.class).getBody();
        log.info("syncData callApi {} response {}", uri, response);
        return response;
    }
}
