package com.tinystop.sjp.service;

import static com.tinystop.sjp.type.ErrorCode.ALREADY_EXIST_USER;
import static com.tinystop.sjp.type.ErrorCode.ID_NOT_FOUND;
import static com.tinystop.sjp.type.ErrorCode.INCORRECT_PASSWORD;

//import org.springframework.http.HttpStatus;

import com.tinystop.sjp.exception.CustomException;
import com.tinystop.sjp.entity.AccountEntity;
import com.tinystop.sjp.dto.CreateAccountDto.SignUp;
import com.tinystop.sjp.dto.SigninDto;
import com.tinystop.sjp.repository.AccountRepository;

import org.springframework.stereotype.Service;
//import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserDetailService {
    
    private final AccountRepository accountRepository;

    public AccountEntity signUp(SignUp user) {
        boolean exists = this.accountRepository.existsByUsername(user.getUsername());
        if (exists) {
            throw new CustomException(ALREADY_EXIST_USER);
            //throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Existed ID Found");// error 작성
        }
        return this.accountRepository.save(user.toEntity());
    }
    public AccountEntity signIn(SigninDto user) {
        AccountEntity accountEntity = this.accountRepository.findByUsername(user.getUsername()).get();
        boolean exists = this.accountRepository.existsByUsername(user.getUsername());
        if (exists) {
            boolean correctPassword = this.accountRepository.passwordCorrect(user.getPassword()); 
            if (!correctPassword) {
                throw new CustomException(INCORRECT_PASSWORD);
                //throw new ResponseStatusException(HttpStatus.NOT_FOUND, "INCORRECT PASSWORD");
            }
        }
        else {
            throw new CustomException(ID_NOT_FOUND);
           // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID NOT FOUND");
        }
        return accountEntity;
    }
    
}
