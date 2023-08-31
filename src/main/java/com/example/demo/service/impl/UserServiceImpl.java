package com.example.demo.service.impl;

import com.example.demo.constant.ResponseStatusConstant;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.dto.SignInResponse;
import com.example.demo.model.entity.Account;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.util.BCryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtServiceImpl jwtService;

    public Account getUser(String username, String password){
        Optional<Account> opUser = userRepository.getByUsername(username);
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
        return userRepository.save(account);
    }

    public SignInResponse signIn(String username, String password){
        Account account = getUser(username, password);
        return SignInResponse.builder()
                .accessToken(jwtService.generateTokenLogin(account))
                .build();
    }
}
