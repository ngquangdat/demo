package com.example.demo.service.impl;

import com.example.demo.constant.ResponseStatusConstant;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.entity.RefreshToken;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.service.RefreshTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${refresh-token.expired-time}")
    private int refreshTokenExpiredTime;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Override
    public String createRefreshToken(String accountId) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, refreshTokenExpiredTime);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setAccountId(accountId);
        refreshToken.setExpiredTime(calendar.getTime());
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    @Override
    public RefreshToken isValidRefreshToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException(ResponseStatusConstant.REFRESH_TOKEN_NOT_EXIST, null));
        if (token.getExpiredTime().compareTo(new Date()) < 0) {
            refreshTokenRepository.delete(token);
            throw new BusinessException(ResponseStatusConstant.REFRESH_TOKEN_EXPIRED, token);
        }
        return token;
    }
}
