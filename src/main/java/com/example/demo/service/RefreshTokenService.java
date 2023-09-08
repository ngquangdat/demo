package com.example.demo.service;

import com.example.demo.model.entity.RefreshToken;

public interface RefreshTokenService {
    String createRefreshToken(String accountId);

    RefreshToken isValidRefreshToken(String token, String authorization);
}
