package com.example.demo.controller;

import com.example.demo.factory.response.ResponseFactory;
import com.example.demo.model.entity.RefreshToken;
import com.example.demo.service.RefreshTokenService;
import com.example.demo.service.UserService;
import com.example.demo.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserValidator userValidator;

    @PostMapping(value = "/sign-up")
    public Object signUp(@RequestParam(name = "username") String username,
                         @RequestParam(name = "password") String password,
                         @RequestParam(name = "phone") String phone,
                         @RequestParam(name = "point") Integer point) {
        userValidator.validateSignUp(username, password);
        return ResponseFactory.success(userService.createUser(username, password, phone, point));
    }

    @PostMapping(value = "/sign-in")
    public Object signIn(@RequestParam(name = "username") String username,
                         @RequestParam(name = "password") String password) {
        return ResponseFactory.success(userService.signIn(username, password));
    }

    @PostMapping(value = "/refresh-token")
    public Object refreshToken(@RequestParam(name = "refreshToken") String refreshToken) {
        RefreshToken token = refreshTokenService.isValidRefreshToken(refreshToken);
        return ResponseFactory.success(userService.signInRefreshToken(token.getAccountId(), refreshToken));
    }
}
