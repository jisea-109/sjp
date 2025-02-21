package com.tinystop.sjp.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tinystop.sjp.dto.CreateAccountDto.SignUp;
import com.tinystop.sjp.dto.SigninDto;
import com.tinystop.sjp.service.UserDetailService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class AccountController {

    private final UserDetailService userService;

    @GetMapping("signupPage") // signup page 
    public String signup() {
        return "signup";
    }
    
    @PostMapping("signup")
    public String signup(@ModelAttribute SignUp signupRequest) {
        this.userService.signUp(signupRequest);
        return "signin";
    }
    @GetMapping("signinPage") // signin page 
    public String signin() {
        return "signin";
    }
    
    @PostMapping("signin")
    public String signIn(@ModelAttribute SigninDto signinRequest) {
        userService.signIn(signinRequest);
        return "index";
    }
    @GetMapping("signout")
    public String signOut() {
        SecurityContextHolder.clearContext();
        return "index";
    }

    @GetMapping("home")
    public String home() {
        return "index";
    }
   
}