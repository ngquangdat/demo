package com.example.demo.service;

import com.example.demo.entity.ApiSyncConfig;
import com.example.demo.repository.ApiSyncConfigRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerJobService {

    private final ThreadPoolTaskScheduler taskScheduler;

    private final ApiSyncConfigRepository apiSyncConfigRepository;

    private final SyncDataService syncDataService;

    private final Map<String, ScheduledFuture<?>> scheduledTasks = new HashMap<>();

    @PostConstruct
    public void init() {
        // Load and schedule tasks on application startup
        refreshSchedules();
    }

    @Scheduled(cron = "0 * * * * ?")
    public void refreshSchedules() {
        log.info("Refresh scheduler");
        // Cancel existing tasks
        scheduledTasks.values().forEach(task -> task.cancel(false));
        scheduledTasks.clear();

        // Load all enabled schedules from database
        List<ApiSyncConfig> configs = apiSyncConfigRepository.findAll();
        for (ApiSyncConfig config : configs) {
            if (StringUtils.isNoneBlank(config.getCron())) {
                scheduleTask(config);
            }
        }
    }

    private void scheduleTask(ApiSyncConfig config) {
        Runnable task = () -> {
            try {
                syncDataService.syncData(config.getSyncCode());
            } catch (Exception e) {
                log.error("Error executing task {}", config.getSyncCode(), e);
                scheduleRetry(config);
            }
        };

        // Schedule the task with cron expression
        ScheduledFuture<?> future = taskScheduler.schedule(task, new CronTrigger(config.getCron()));
        scheduledTasks.put(config.getSyncCode(), future);
    }

    private void scheduleRetry(ApiSyncConfig config) {
        Runnable retryTask = () -> {
            try {
                syncDataService.syncData(config.getSyncCode());
            } catch (Exception e) {
                log.error("Error executing retry task {}", config.getSyncCode(), e);
                scheduleRetry(config);
            }
        };

        // Schedule retry after 5 minutes
        Instant retryTime = Instant.now().plus(5, ChronoUnit.MINUTES);
        taskScheduler.schedule(retryTask, retryTime);
    }
}
