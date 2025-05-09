package com.tinystop.sjp.Auth.Email;

import static com.tinystop.sjp.Type.ErrorCode.FAILED_TO_READ_TEMPLATE;
import static com.tinystop.sjp.Type.ErrorCode.FAILED_TO_SEND_EMAIL;
import static com.tinystop.sjp.Type.ErrorCode.INCORRECT_SECURITY_CODE;
import static com.tinystop.sjp.Type.ErrorCode.SECURITY_CODE_EXPIRED;
import static com.tinystop.sjp.Type.ErrorCode.ALREADY_EXIST_EMAIL;

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
    private final AccountRepository accountRepository;
    private final int EMAIL_SECURE_CODE_EXIRATION_TIME = 5; // 이메일 인증코드 만료 시간 (5분)

    /** 보안 인증코드 보내기
     * - 6개의 숫자와 알파벳이 섞인 보안코드가 담긴 템플릿을 JavaMailSender 을 통해서 유저한테 전송함.
     * @param email 인증코드 받을 이메일 
     */
    public void SendSecurityCode(String email) { // 이메일 인증코드 보내기
        if (accountRepository.existsByEmail(email)) { // 이미 회원가입된 이메일이 있으면 throw
            throw new CustomException(ALREADY_EXIST_EMAIL, "signup");
        }

        String securityCode = SecurityCode(); // 보안코드 생성

        LocalDateTime expiry = LocalDateTime.now().plusMinutes(EMAIL_SECURE_CODE_EXIRATION_TIME); // 5분 카운트
        EmailVerificationEntity verification = emailVerificationRepository.findByEmail(email).orElse(new EmailVerificationEntity()); // 인증 이메일 임시 저장

        // 임시 저장한 인증 이메일 설정
        verification.setEmail(email);
        verification.setCode(securityCode);
        verification.setExpiryTime(expiry);
        verification.setVerified(false);
        emailVerificationRepository.save(verification);

        try { // HTML 템플릿 읽기
            String templateToEmail;
            // 이메일 전송은 SMTP(Simple Mail Transfer Protocol)을 통해 이루어지기 때문에, html을 문자열로 전송하기 위해서 BufferReader 사용
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(template.getInputStream(), StandardCharsets.UTF_8))) {
                templateToEmail = reader.lines().collect(Collectors.joining("\n")); // html파일을 문자열로 변환
            }

            templateToEmail = templateToEmail.replace("${securityCode}", securityCode.toString()); // 보안코드 html에 넣기

            MimeMessage mimeMessage = javaMailSender.createMimeMessage(); // 메일 생성
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8"); // (MimeMessage, boolean multipart, @Nullable String encoding)

            helper.setTo(email); // 이메일 주소
            helper.setSubject("TinyStop 보안코드"); // 이메일 제목
            helper.setFrom("sejin991009@gmail.com"); // 이메일 설정
            helper.setText(templateToEmail, true); // HTML 형식

            javaMailSender.send(mimeMessage); // 전송
        // 오류가 있을 시 error throw
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
