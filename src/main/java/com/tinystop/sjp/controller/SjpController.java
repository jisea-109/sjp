package com.tinystop.sjp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SjpController {
    @GetMapping("/signin")
    public String signin() {
        return "signin";
    }
    @GetMapping("/home")
    public String home() {
        return "index";
    }
}