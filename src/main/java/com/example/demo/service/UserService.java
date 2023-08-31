package com.example.demo.service;

import com.example.demo.model.dto.SignInResponse;
import com.example.demo.model.entity.Account;

public interface UserService {

    Account getUser(String username, String password);

    Account createUser(String username, String password, String phone, Integer point);

    SignInResponse signIn(String username, String password);
}
