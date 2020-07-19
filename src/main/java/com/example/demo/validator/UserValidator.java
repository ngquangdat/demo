package com.example.demo.validator;

import com.example.demo.constant.ResponseStatusConstant;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.entity.User;
import com.example.demo.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.Optional;

@Component
public class UserValidator {

    @Autowired
    private UserRepository userRepositoryInject;

    private static UserRepository userRepository;

    @PostConstruct
    private void initStaticDao () {
        userRepository = this.userRepositoryInject;
    }

    public static void validateSignUp(String username, String password){
        if(StringUtils.isBlank(username) || StringUtils.isBlank(password)){
            throw new BusinessException(ResponseStatusConstant.BAD_REQUEST, null, null);
        }

        Optional<User> opUser = userRepository.getByUsername(username);
        if(opUser.isPresent()){
            throw new BusinessException(ResponseStatusConstant.USER_EXISTED, null, null);
        }
    }
}
