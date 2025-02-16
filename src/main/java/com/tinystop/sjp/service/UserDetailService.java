package com.tinystop.sjp.service;

import org.springframework.stereotype.Service;

import com.tinystop.sjp.dto.CreateAccountDto.SignUp;
import com.tinystop.sjp.dto.SigninDto;
import com.tinystop.sjp.entity.AccountEntity;
import com.tinystop.sjp.exception.CustomException;
import com.tinystop.sjp.repository.AccountRepository;
import static com.tinystop.sjp.type.ErrorCode.ALREADY_EXIST_USER;
import static com.tinystop.sjp.type.ErrorCode.ID_NOT_FOUND;
import static com.tinystop.sjp.type.ErrorCode.INCORRECT_PASSWORD;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserDetailService {
    
    private final AccountRepository accountRepository;

    public AccountEntity signUp(SignUp user) {
        boolean exists = this.accountRepository.existsByUsername(user.getUsername());
        if (exists) {
            throw new CustomException(ALREADY_EXIST_USER);
        }
        return this.accountRepository.save(user.toEntity());
    }
    
    public SigninDto.Response signIn(SigninDto user) {
        AccountEntity accountEntity = this.accountRepository.findByUsername(user.getUsername()).orElseThrow(() -> new CustomException(ID_NOT_FOUND));
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID NOT FOUND");
        boolean exists = this.accountRepository.existsByUsername(user.getUsername());
        if (exists) {
            if (!accountEntity.getPassword().equals(user.getPassword())) {
                throw new CustomException(INCORRECT_PASSWORD);
                //throw new ResponseStatusException(HttpStatus.NOT_FOUND, "INCORRECT PASSWORD");
            }
        }
        return SigninDto.Response.from(accountEntity);
    }
    
}
