package com.example.demo.controller;

import com.example.demo.service.SyncDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SyncDataController {

    private final SyncDataService syncDataService;

    @GetMapping("/sync-data")
    public void syncData(@RequestParam String syncCode) {
        syncDataService.syncData(syncCode);
    }
}
