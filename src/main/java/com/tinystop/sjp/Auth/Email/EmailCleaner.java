package com.tinystop.sjp.Auth.Email;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@EnableScheduling
public class EmailCleaner { // 이메일 인증만 하고 회원가입 안하는 유저들이 남기고 간 이메일들 삭제

    private final EmailVerificationRepository emailVerificationRepository;

    @Scheduled(fixedRate = 3600000) // 1시간
    public void cleanExpiredVerifications() {
        emailVerificationRepository.deleteExpiredEmails(LocalDateTime.now());
    }
}
