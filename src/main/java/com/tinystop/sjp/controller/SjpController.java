package com.tinystop.sjp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tinystop.sjp.dto.CreateAccountDto.SignUp;
import com.tinystop.sjp.dto.CreateAccountDto.SignUpResponse;
import com.tinystop.sjp.dto.SigninDto;
import com.tinystop.sjp.entity.AccountEntity;
import com.tinystop.sjp.service.UserDetailService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor

public class SjpController {
    private final UserDetailService userService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@ModelAttribute SignUp signupRequest) {
        AccountEntity accountEntity = this.userService.signUp(signupRequest);
        return ResponseEntity.ok(
            SignUpResponse.builder()
            .username(accountEntity.getUsername())
            .email(accountEntity.getEmail())
            .build());
    }
    // @PostMapping("/signin")
    // public ResponseEntity<String> signIn(@ModelAttribute SigninDto signinRequest) {
    //     AccountEntity accountEntity = this.userService.signIn(signinRequest);
        
    // }
    
}