package com.example.demo.service;

import com.example.demo.model.entity.Account;

import java.util.Map;


public interface JwtService {
    String generateTokenLogin(Account account);

    String validateJwtToken(String token);

    Map<String, Object> getPayload(String token);
}
