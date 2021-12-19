package com.example.demo.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FilesStorageService {
    void init();
    void save(MultipartFile file);
    Resource load(String filename);
    void deleteAll();
    Resource convertWebmToMp4(MultipartFile file);
}
