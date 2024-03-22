//package com.example.demo.controller;
//
//import com.example.demo.factory.response.ResponseFactory;
//import com.example.demo.repository.AccountRepository;
//import com.example.demo.repository.RefreshTokenRepository;
//import com.example.demo.service.JwtService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.web.bind.annotation.*;
//
//@Slf4j
//@RestController
//public class DemoController {
//    @Autowired
//    private JwtService jwtService;
//
//    @Autowired
//    private AccountRepository accountRepository;
//
//    @Autowired
//    private RefreshTokenRepository refreshTokenRepository;
//
//    @GetMapping
//    public String hello() {
//        return "Hello world!";
//    }
//
//    @GetMapping("/account")
//    public Object getAccounts() {
//        return ResponseFactory.success(accountRepository.findAll());
//    }
//
//    @GetMapping("/token")
//    public Object getTokens() {
//        return ResponseFactory.success(refreshTokenRepository.findAll());
//    }
//
//    @GetMapping("/payload")
//    public Object getPayload(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
//        return ResponseFactory.success(jwtService.getPayload(authorization.substring(7)));
//    }
//}
