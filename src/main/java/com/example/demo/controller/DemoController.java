package com.example.demo.controller;

import com.example.demo.factory.response.ResponseFactory;
import com.example.demo.repository.CityRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.FilesStorageService;
import com.example.demo.service.JwtService;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
public class DemoController {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FilesStorageService storageService;

    @GetMapping
    public String hello() {
        return "Hello world!";
    }

    @GetMapping("/city")
    public Object getCities() {
        return ResponseFactory.success(cityRepository.findAll());
    }

    @GetMapping("/user")
    public Object getUsers() {
        return ResponseFactory.success(userRepository.findAll());
    }

    @PostMapping("/convert-webm-mp4")
    @ResponseBody
    public ResponseEntity<Resource> uploadFile(@RequestParam("file") MultipartFile file) {
        Resource result = storageService.convertWebmToMp4(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + ".mp4\"").body(result);
    }
}
