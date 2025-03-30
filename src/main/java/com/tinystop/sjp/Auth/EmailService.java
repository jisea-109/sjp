package com.tinystop.sjp.Auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.tinystop.sjp.exception.CustomException;

import static com.tinystop.sjp.type.ErrorCode.EXPIRED_OR_INCORRECT_CODE;
import static com.tinystop.sjp.type.ErrorCode.FAILED_TO_READ_TEMPLATE;
import static com.tinystop.sjp.type.ErrorCode.FAILED_TO_SEND_EMAIL;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("classpath:templates/template-to-email.html") // 경로 설정
    private Resource template;

    private final JavaMailSender javaMailSender;

    public void SendSecurityCode(String email, String securityCode, HttpSession session) {
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
        session.setAttribute("emailCode:" + email, securityCode); // 세션 저장
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

    public boolean VerifySecurityCode(String email, String inputCode, HttpSession session) {
        String expectedCode = (String) session.getAttribute("emailCode:" + email);
        if (expectedCode == null) { // 세션 확인 안될시
            throw new CustomException(EXPIRED_OR_INCORRECT_CODE, "signup");
        }
        if (expectedCode.equals(inputCode)) { // 인증 성공후 세션 삭제
            session.removeAttribute("emailCode:" + email); 
            session.setAttribute("emailVerified:" + email, email);
            return true;
        }

        return false; // 인증 안되면 false 리턴
    }
}
