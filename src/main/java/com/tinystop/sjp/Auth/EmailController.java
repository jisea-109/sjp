package com.tinystop.sjp.Auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email/")
public class EmailController {
    
    private final EmailService emailService;

    @PostMapping("send-code")
    public ResponseEntity<String> SendSecurityCode(@RequestParam("email") String email, HttpSession session) {
        String code = emailService.SecurityCode();
        emailService.SendSecurityCode(email, code, session);
        return ResponseEntity.ok("이메일로 인증번호 전송");
    }

    @PostMapping("verify-code")
    public ResponseEntity<String> VerifySecurityCode(@RequestParam("email") String email,
                                                     @RequestParam("code") String code,
                                                     HttpSession session) {
        boolean verified = emailService.VerifySecurityCode(email, code, session);
        if (verified) {
            return ResponseEntity.ok("인증 성공");
        } else {
            return ResponseEntity.badRequest().body("인증 실패");
        }
    }
}
