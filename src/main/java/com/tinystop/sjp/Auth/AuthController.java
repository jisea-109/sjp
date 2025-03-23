package com.tinystop.sjp.Auth;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("signupPage") // signup page 
    public String signupPage(Model model) {
        model.addAttribute("signup", new SignUpDto());
        return "signup";
    }
    
    @PostMapping("signup")
    public String signup(@ModelAttribute("signup") @Valid SignUpDto signupRequest, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "signup";
        }
        this.authService.signUp(signupRequest);
        return "signin";
    }
    @GetMapping("signinPage") // signin page 
    public String signinPage(Model model) {
        model.addAttribute("signin", new SigninDto());
        return "signin";
    }
    
    @PostMapping("signin")
    public String signIn(@ModelAttribute("signin") @Valid SigninDto signinRequest, BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "signin";
        }
        authService.signIn(signinRequest, session);
        return "redirect:/";
    }
    @GetMapping("signout")
    public String signout(HttpServletRequest request, HttpServletResponse response) { //(클라이언트에서 보낸 HTTP 요청, 서버가 클라이언트에게 응답을 보낼 객체)
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication()); // SecurityContext에서 인증 정보 삭제
        return "redirect:/";
    }
    @GetMapping("")
    public String home() {
        return "index";
    }
   
}