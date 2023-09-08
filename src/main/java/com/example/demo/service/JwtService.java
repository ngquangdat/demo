package com.example.demo.service;

import com.example.demo.model.entity.Account;

import java.util.Calendar;
import java.util.Map;


public interface JwtService {
    String generateTokenLogin(Account account);

    String generateRefreshToken(Calendar calendar, String refreshTokenId);

    String validateJwtToken(String token);

    String validateJwtTokenExpired(String token);

    Map<String, Object> getPayload(String token);

    String getJti(String token);
}
