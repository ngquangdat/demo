package com.example.demo.validator;

import com.example.demo.constant.ResponseStatusConstant;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.entity.Account;
import com.example.demo.repository.AccountRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class UserValidator {

    @Autowired
    private AccountRepository accountRepository;


    public void validateSignUp(String username, String password){
        if(StringUtils.isBlank(username) || StringUtils.isBlank(password)){
            throw new BusinessException(ResponseStatusConstant.BAD_REQUEST, null);
        }

        Optional<Account> opUser = accountRepository.getByUsername(username);
        if(opUser.isPresent()){
            throw new BusinessException(ResponseStatusConstant.USER_EXISTED, null);
        }
    }
}
