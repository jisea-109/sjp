package com.tinystop.sjp.service;

import org.springframework.http.HttpStatus;

import com.tinystop.sjp.entity.AccountEntity;
import com.tinystop.sjp.dto.CreateAccountDto.SignUp;
import com.tinystop.sjp.dto.SigninDto;
import com.tinystop.sjp.repository.AccountRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserDetailService {
    
    private final AccountRepository accountRepository;

    public AccountEntity signUp(SignUp user){
        boolean exists = this.accountRepository.existsByUsername(user.getUsername());
        if (exists) {
            // error 작성
        }
        return this.accountRepository.save(user.toEntity());
    }
}
