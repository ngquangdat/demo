package com.example.demo.service;

import com.example.demo.constant.ResponseStatusConstant;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.BCryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    public User getUser(String username, String password){
        Optional<User> opUser = userRepository.getByUsername(username);
        if(opUser.isPresent()){
            User user = opUser.get();
            if(BCryptUtil.check(password, user.getPassword())){
                return user;
            }
        }
        throw new BusinessException(ResponseStatusConstant.LOGIN_FAILED, null, null);
    }

    public String login(String username, String password){
        User user = getUser(username, password);
        return jwtService.generateTokenLogin(user);
    }
}
