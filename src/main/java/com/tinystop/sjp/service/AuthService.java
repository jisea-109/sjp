package com.tinystop.sjp.service;

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
import com.tinystop.sjp.dto.CreateAccountDto.SignUp;
import com.tinystop.sjp.dto.SigninDto;
import com.tinystop.sjp.entity.AccountEntity;
import com.tinystop.sjp.exception.CustomException;
import com.tinystop.sjp.repository.AccountRepository;

import static com.tinystop.sjp.type.ErrorCode.ALREADY_EXIST_USER;
import static com.tinystop.sjp.type.ErrorCode.ID_NOT_FOUND;
import static com.tinystop.sjp.type.ErrorCode.INCORRECT_PASSWORD;

@RequiredArgsConstructor
@Transactional
@Service
public class AuthService {
    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    
    public AccountEntity signUp(SignUp user) {
        boolean exists = this.accountRepository.existsByUsername(user.getUsername());
        if (exists) {
            throw new CustomException(ALREADY_EXIST_USER);
        }
        String encodedPassword = passwordEncoder.encode(user.getPassword()); // 비밀번호 암호화

        return this.accountRepository.save(user.toEntity(encodedPassword));
    }
    
    public AccountEntity signIn(SigninDto user, HttpSession session) {
        AccountEntity accountEntity = this.accountRepository.findByUsername(user.getUsername()).orElseThrow(() -> new CustomException(ID_NOT_FOUND));
        if (!passwordEncoder.matches(user.getPassword(), accountEntity.getPassword())) {
            throw new CustomException(INCORRECT_PASSWORD);
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