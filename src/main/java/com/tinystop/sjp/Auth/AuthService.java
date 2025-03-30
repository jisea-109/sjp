package com.tinystop.sjp.Auth;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import java.util.Collections;

import com.tinystop.sjp.exception.CustomException;

import static com.tinystop.sjp.type.ErrorCode.ALREADY_EXIST_USER;
import static com.tinystop.sjp.type.ErrorCode.ID_NOT_FOUND;
import static com.tinystop.sjp.type.ErrorCode.INCORRECT_PASSWORD;
import static com.tinystop.sjp.type.ErrorCode.USER_NOT_FOUND;
import static com.tinystop.sjp.type.ErrorCode.DUPLICATE_EMAIL_FOUND;
import static com.tinystop.sjp.type.ErrorCode.EMAIL_NOT_VERIFIED;
import static com.tinystop.sjp.type.ErrorCode.EXPIRED_OR_INCORRECT_CODE;


@RequiredArgsConstructor
@Transactional
@Service
public class AuthService {
    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    
    public AccountEntity signUp(SignUpDto user, HttpSession session) { // 회원가입

        if (this.accountRepository.existsByUsername(user.getUsername())) { // 중복 유저 확인
            throw new CustomException(ALREADY_EXIST_USER,"signup");
        }
        if (accountRepository.existsByEmail(user.getEmail())) { // 중복 이메일 확인
            throw new CustomException(DUPLICATE_EMAIL_FOUND,"signup"); 
        }
        if (session.getAttribute("emailVerified:" + user.getEmail()) == null || !session.getAttribute("emailVerified:" + user.getEmail()).equals(user.getEmail())) { // 이메일 인증 체크
            System.out.println(session.getAttribute("emailVerified:"+user.getEmail()));
            throw new CustomException(EMAIL_NOT_VERIFIED,"signup");
        }

        session.removeAttribute("emailVerified:"+user.getEmail()); // 이메일 인증 세션 삭제
        String encodedPassword = passwordEncoder.encode(user.getPassword()); // 비밀번호 암호화

        return this.accountRepository.save(user.toEntity(encodedPassword));
    }
    
    public AccountEntity signIn(SigninDto user, HttpSession session) { // 로그인

        AccountEntity accountEntity = this.accountRepository.findByUsername(user.getUsername()).orElseThrow(() -> new CustomException(USER_NOT_FOUND,"signin")); // 유저 있는지 확인
        if (!passwordEncoder.matches(user.getPassword(), accountEntity.getPassword())) { // 비밀번호 확인
            throw new CustomException(INCORRECT_PASSWORD,"signin");
        }

        String roleName = "ROLE_" + accountEntity.getRole().name().replace("ROLE_", ""); // ROLE_ 를 추가해야 USER로 인식함
        UserDetails userDetails = new User(
        accountEntity.getUsername(),
        accountEntity.getPassword(),
        Collections.singletonList(new SimpleGrantedAuthority(roleName))
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        ); // (인증완료된 사용자 객체, 비밀번호(이미 앞에서 객체 등록해서 null 로 지정), 권한 목록)

        SecurityContext securityContext = SecurityContextHolder.getContext(); // SecurityContextHolder = 현재 로그인한 사용자의 인증 정보를 저장하는 객체
        securityContext.setAuthentication(authentication); // 로그인한 사용자의 인증 정보 SecurityContextHolder에 저장

        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext); // SecurityContext를 세션에 저장
        return accountEntity;
    }
    
}