package com.example.demo.service.impl;

import com.example.demo.constant.ResponseStatusConstant;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.dto.SignInResponse;
import com.example.demo.model.entity.Account;
import com.example.demo.repository.AccountRepository;
import com.example.demo.service.JwtService;
import com.example.demo.service.RefreshTokenService;
import com.example.demo.service.UserService;
import com.example.demo.util.BCryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountServiceImpl implements UserService {

    @Autowired
    private AccountRepository accountRepository;

//    @Autowired
//    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    public Account getUser(String username, String password){
        Optional<Account> opUser = accountRepository.getByUsername(username);
        if(opUser.isPresent()){
            Account account = opUser.get();
            if(BCryptUtil.check(password, account.getPassword())){
                return account;
            }
        }
        throw new BusinessException(ResponseStatusConstant.SIGNIN_FAILED, null);
    }

    public Account createUser(String username, String password, String phone, Integer point){
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(BCryptUtil.hash(password));
        account.setPhone(phone);
        account.setPoint(point);
        return accountRepository.save(account);
    }

    public SignInResponse signIn(String username, String password){
        Account account = getUser(username, password);
        return SignInResponse.builder()
//                .accessToken(jwtService.generateTokenLogin(account))
                .refreshToken(refreshTokenService.createRefreshToken(account.getId()))
                .build();
    }

    @Override
    public SignInResponse signInRefreshToken(String accountId, String refreshToken) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(ResponseStatusConstant.SIGNIN_FAILED, null));
        return SignInResponse.builder()
//                .accessToken(jwtService.generateTokenLogin(account))
                .refreshToken(refreshToken)
                .build();
    }
}
