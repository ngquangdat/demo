package com.example.demo.controller;

import com.example.demo.factory.response.ResponseFactory;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.JwtService;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class DemoController {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String hello() {
        return "Hello world!";
    }

    @GetMapping("/user")
    public Object getUsers() {
        return ResponseFactory.success(userRepository.findAll());
    }

    @GetMapping("/payload")
    public Object getPayload(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return ResponseFactory.success(jwtService.getPayload(authorization.substring(7)));
    }
}
