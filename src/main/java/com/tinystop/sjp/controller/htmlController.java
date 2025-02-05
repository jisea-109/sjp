package com.tinystop.sjp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class htmlController {
    @GetMapping("/signinPage")
    public String signin() {
        return "signin";
    }
    @GetMapping("/signupPage")
    public String asdf() {
        return "signup";
    }
    @GetMapping("/home")
    public String home() {
        return "index";
    }
}
