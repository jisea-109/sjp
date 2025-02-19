package com.tinystop.sjp.service;

import org.springframework.stereotype.Service;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.tinystop.sjp.dto.CreateAccountDto.SignUp;
import com.tinystop.sjp.dto.SigninDto;
import com.tinystop.sjp.entity.AccountEntity;
import com.tinystop.sjp.exception.CustomException;
import com.tinystop.sjp.repository.AccountRepository;
import static com.tinystop.sjp.type.ErrorCode.ALREADY_EXIST_USER;
import static com.tinystop.sjp.type.ErrorCode.ID_NOT_FOUND;
import static com.tinystop.sjp.type.ErrorCode.INCORRECT_PASSWORD;

@Transactional
@Service
public class UserDetailService {
    
    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserDetailService(AccountRepository accountRepository, BCryptPasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public AccountEntity signUp(SignUp user) {
        boolean exists = this.accountRepository.existsByUsername(user.getUsername());
        if (exists) {
            throw new CustomException(ALREADY_EXIST_USER);
        }
        String encodedPassword = passwordEncoder.encode(user.getPassword());

        return this.accountRepository.save(user.toEntity(encodedPassword));
    }
    
    public SigninDto.Response signIn(SigninDto user) {
        AccountEntity accountEntity = this.accountRepository.findByUsername(user.getUsername()).orElseThrow(() -> new CustomException(ID_NOT_FOUND));
        if (!passwordEncoder.matches(user.getPassword(), accountEntity.getPassword())) {
            throw new CustomException(INCORRECT_PASSWORD);
        }
        Authentication authentication = new UsernamePasswordAuthenticationToken(accountEntity.getUsername(), null, null); // 사용자 정보 인증
        SecurityContextHolder.getContext().setAuthentication(authentication); // 사용자 정보 저장
        return SigninDto.Response.from(accountEntity);
    }
    
}
