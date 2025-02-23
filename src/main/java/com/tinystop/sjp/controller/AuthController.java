package com.tinystop.sjp.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tinystop.sjp.dto.CreateAccountDto.SignUp;
import com.tinystop.sjp.dto.SigninDto;
import com.tinystop.sjp.service.AuthService;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("signupPage") // signup page 
    public String signup() {
        return "signup";
    }
    
    @PostMapping("signup")
    public String signup(@ModelAttribute SignUp signupRequest) {
        this.authService.signUp(signupRequest);
        return "signin";
    }
    @GetMapping("signinPage") // signin page 
    public String signin() {
        return "signin";
    }
    
    @PostMapping("signin")
    public String signIn(@ModelAttribute SigninDto signinRequest, HttpSession session) {
        authService.signIn(signinRequest, session);
        return "redirect:/";
    }
    @GetMapping("signout")
    public String signOut(HttpServletRequest request, HttpServletResponse response) { //(클라이언트에서 보낸 HTTP 요청, 서버가 클라이언트에게 응답을 보낼 객체)
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication()); // SecurityContext에서 인증 정보 삭제
        return "redirect:/";
    }
    @GetMapping("")
    public String home() {
        return "index";
    }
   
}