package com.tinystop.sjp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tinystop.sjp.dto.CreateAccountDto.SignUp;
import com.tinystop.sjp.dto.CreateAccountDto.SignUpResponse;
import com.tinystop.sjp.dto.SigninDto;
import com.tinystop.sjp.dto.SigninDto.Response;
import com.tinystop.sjp.entity.AccountEntity;
import com.tinystop.sjp.service.UserDetailService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SjpController {

    private final UserDetailService userService;

    @GetMapping("/signupPage")
    public String signup() {
        return "signup";
    }
    
    @PostMapping("/signup")
    public String signup(@ModelAttribute SignUp signupRequest) {
        AccountEntity accountEntity = this.userService.signUp(signupRequest);
        return "signin";
    }
    
    @GetMapping("/signinPage")
    public String signin() {
        return "signin";
    }
    
    @PostMapping("/signin")
    public String signIn(@ModelAttribute SigninDto signinRequest) {
        SigninDto.Response accountEntity = this.userService.signIn(signinRequest);
        return "index";
    }

    @GetMapping("/home")
    public String home() {
        return "index";
    }
   
}