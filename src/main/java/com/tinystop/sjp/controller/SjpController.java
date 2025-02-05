package com.tinystop.sjp.controller;

import com.tinystop.sjp.dto.CreateAccountDto.SignUp;
import com.tinystop.sjp.dto.CreateAccountDto.SignUpResponse;
import com.tinystop.sjp.entity.AccountEntity;
import com.tinystop.sjp.service.UserDetailService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;

@RestController
@RequiredArgsConstructor

public class SjpController {
    private final UserDetailService userService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@RequestParam(value="id") SignUp signupRequest) {
        AccountEntity accountEntity = this.userService.signUp(signupRequest);
        return ResponseEntity.ok(
            SignUpResponse.builder()
            .username(accountEntity.getUsername())
            .password(accountEntity.getPassword())
            .email(accountEntity.getEmail())
            .build());
    }
}