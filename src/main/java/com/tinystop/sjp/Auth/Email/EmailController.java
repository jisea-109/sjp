package com.tinystop.sjp.Auth.Email;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email/")
public class EmailController {
    
    private final EmailService emailService;

    @PostMapping("send-code")
    public ResponseEntity<Map<String, String>> SendSecurityCode(@RequestParam("email") String email) {
    emailService.SendSecurityCode(email);
    Map<String, String> response = new HashMap<>();
    response.put("message", "이메일로 인증번호 전송");
    return ResponseEntity.ok(response);
}

    @PostMapping("verify-code")
    public ResponseEntity<String> VerifySecurityCode(@RequestParam("email") String email, @RequestParam("code") String code) {
        boolean verified = emailService.VerifySecurityCode(email, code);
        if (verified) {
            return ResponseEntity.ok("인증 성공");
        }
        return ResponseEntity.badRequest().body("인증 실패");
    }
}
