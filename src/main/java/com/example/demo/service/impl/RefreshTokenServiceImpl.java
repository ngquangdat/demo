package com.example.demo.service.impl;

import com.example.demo.constant.ResponseStatusConstant;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.entity.RefreshToken;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.service.JwtService;
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

//    @Autowired
//    private JwtService jwtService;

    @Override
    public String createRefreshToken(String accountId) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, refreshTokenExpiredTime);
        String refreshTokenId = UUID.randomUUID().toString();
        String token = "jwtService.generateRefreshToken(calendar, refreshTokenId)";
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setAccountId(accountId);
        refreshToken.setExpiredTime(calendar.getTime());
        refreshToken.setTokenId(refreshTokenId);
        refreshTokenRepository.save(refreshToken);
        return token;
    }

    @Override
    public RefreshToken isValidRefreshToken(String refreshToken, String authorization) {
//        String validateRefreshToken = jwtService.validateJwtToken(refreshToken);
//        if (validateRefreshToken != null) {
//            throw new BusinessException(ResponseStatusConstant.NOT_AUTH, validateRefreshToken);
//        }
//        String validateAuthorization = jwtService.validateJwtTokenExpired(authorization.substring(7));
//        if (validateAuthorization != null) {
//            throw new BusinessException(ResponseStatusConstant.NOT_AUTH, validateAuthorization);
//        }
//        String tokenId = jwtService.getJti(refreshToken);
//        RefreshToken token = refreshTokenRepository.findByTokenId(tokenId)
//                .orElseThrow(() -> new BusinessException(ResponseStatusConstant.REFRESH_TOKEN_NOT_EXIST, null));
//        if (token.getExpiredTime().compareTo(new Date()) < 0) {
//            refreshTokenRepository.delete(token);
//            throw new BusinessException(ResponseStatusConstant.REFRESH_TOKEN_EXPIRED, token);
//        }
//        return token;
        return null;
    }
}
