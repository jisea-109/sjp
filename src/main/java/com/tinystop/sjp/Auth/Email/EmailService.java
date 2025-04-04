package com.tinystop.sjp.Auth.Email;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.tinystop.sjp.Auth.AccountRepository;
import com.tinystop.sjp.Exception.CustomException;

import static com.tinystop.sjp.type.ErrorCode.INCORRECT_SECURITY_CODE;
import static com.tinystop.sjp.type.ErrorCode.FAILED_TO_READ_TEMPLATE;
import static com.tinystop.sjp.type.ErrorCode.FAILED_TO_SEND_EMAIL;
import static com.tinystop.sjp.type.ErrorCode.SECURITY_CODE_EXPIRED;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("classpath:templates/template-to-email.html") // 경로 설정
    private Resource template;

    private final JavaMailSender javaMailSender;
    private final EmailVerificationRepository emailVerificationRepository;
    private final int EMAIL_SECURE_CODE_EXIRATION_TIME = 5;
    public void SendSecurityCode(String email) { // 이메일 인증코드 보내기
        String securityCode = SecurityCode(); // 보안코드 생성

        LocalDateTime expiry = LocalDateTime.now().plusMinutes(EMAIL_SECURE_CODE_EXIRATION_TIME); // 5분 카운트
        EmailVerificationEntity verification = emailVerificationRepository.findByEmail(email).orElse(new EmailVerificationEntity());

        verification.setEmail(email);
        verification.setCode(securityCode);
        verification.setExpiryTime(expiry);
        verification.setVerified(false);

        emailVerificationRepository.save(verification);

        try { // HTML 템플릿 읽기
            String templateToEmail;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(template.getInputStream(), StandardCharsets.UTF_8))) {
                templateToEmail = reader.lines().collect(Collectors.joining("\n"));
            }

            templateToEmail = templateToEmail.replace("${securityCode}", securityCode.toString()); // 보안코드 html에 넣기

            MimeMessage mimeMessage = javaMailSender.createMimeMessage(); // html 파일 메일 전송
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8"); // (MimeMessage, boolean multipart, @Nullable String encoding)

            helper.setTo(email); // 이메일 주소
            helper.setSubject("TinyStop 보안코드"); // 이메일 제목
            helper.setFrom("sejin991009@gmail.com"); // 이메일 설정
            helper.setText(templateToEmail, true); // HTML 형식

            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new CustomException(FAILED_TO_SEND_EMAIL, "signup");
        } catch (IOException e) {
            throw new CustomException(FAILED_TO_READ_TEMPLATE,"signup");
        }
    }

    public String SecurityCode() { // 코드 랜덤조합기
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder securityCode = new StringBuilder();
        SecureRandom ran = new SecureRandom();
    
        for (int i = 0; i < 6; i++) {
            int index = ran.nextInt(chars.length());
            securityCode.append(chars.charAt(index));
        }
    
        return securityCode.toString();
    }

    public boolean VerifySecurityCode(String email, String inputCode) { // 코드 확인
        EmailVerificationEntity verification = emailVerificationRepository.findByEmail(email).orElseThrow(() -> new CustomException(SECURITY_CODE_EXPIRED, "signup"));

        if (LocalDateTime.now().isAfter(verification.getExpiryTime() )) { // 시간 지나면 코드 만료
            throw new CustomException(SECURITY_CODE_EXPIRED, "signup");
        }
    
        if (!verification.getCode().equals(inputCode)) { // 인증코드 다르게 입력하면 throw
            throw new CustomException(INCORRECT_SECURITY_CODE, "signup");
        }

        verification.setVerified(true);  // 인증 완료
        emailVerificationRepository.save(verification); // entity에 저장
        return true;
    }
}
