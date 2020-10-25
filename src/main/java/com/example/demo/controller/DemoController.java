package com.example.demo.controller;

import com.example.demo.factory.response.ResponseFactory;
import com.example.demo.repository.CityRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.JwtService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String hello() {
        return "Hello world!";
    }

    @GetMapping("/city")
    public Object getCities() {
        return ResponseFactory.success(cityRepository.findAll());
    }

    @GetMapping("/user")
    public Object getUsers() {
        return ResponseFactory.success(userRepository.findAll());
    }
}
