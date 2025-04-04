package com.tinystop.sjp.Auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthCheckController {
    private final AccountRepository accountRepository;

    @GetMapping("/check-username")
    public ResponseEntity<String> checkUsername(@RequestParam("username") String username) {
        boolean exists = accountRepository.existsByUsername(username);
        return ResponseEntity.ok(exists ? "EXISTS" : "AVAILABLE");
    }
    
    @GetMapping("/check-email")
    public ResponseEntity<String> checkEmail(@RequestParam("email") String email) {
        boolean exists = accountRepository.existsByEmail(email);
        return ResponseEntity.ok(exists ? "EXISTS" : "AVAILABLE");
    }

}
