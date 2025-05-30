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

import com.tinystop.sjp.Auth.AuthDto.ChangePasswordDto;
import com.tinystop.sjp.Auth.AuthDto.DeleteAccountDto;
import com.tinystop.sjp.Auth.AuthDto.SignUpDto;
import com.tinystop.sjp.Auth.AuthDto.SigninDto;
import com.tinystop.sjp.Auth.Email.EmailVerificationRepository;
import com.tinystop.sjp.Exception.CustomException;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import static com.tinystop.sjp.Type.ErrorCode.ALREADY_EXIST_USER;
import static com.tinystop.sjp.Type.ErrorCode.DUPLICATE_EMAIL_FOUND;
import static com.tinystop.sjp.Type.ErrorCode.EMAIL_NOT_VERIFIED;
import static com.tinystop.sjp.Type.ErrorCode.INCORRECT_PASSWORD;
import static com.tinystop.sjp.Type.ErrorCode.PASSWORD_DOES_NOT_MATCH;
import static com.tinystop.sjp.Type.ErrorCode.USER_NOT_FOUND;

import java.util.Collections;

@RequiredArgsConstructor
@Transactional
@Service
public class AuthService {
    
    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder passwordEncoder; // 블로피시 암호화
    private final EmailVerificationRepository emailVerificationRepository;
    
    /**
     * 회원가입 함수
     * - 중복 유저 및 이메일 확인, 이메일 인증 확인 체크 후 비밀번호 암호화해서 account entity save
     * @param user 회원가입에 필요한 username, password, email 이 담긴 유저 정보 dto
     */
    public void signUp(SignUpDto user) {

        if (this.accountRepository.existsByUsername(user.getUsername())) { // 중복 유저 확인
            throw new CustomException(ALREADY_EXIST_USER,"signup");
        }
        if (accountRepository.existsByEmail(user.getEmail())) { // 중복 이메일 확인
            throw new CustomException(DUPLICATE_EMAIL_FOUND,"signup"); 
        }
        if (!emailVerificationRepository.existsByEmailAndVerifiedTrue(user.getEmail())) { // 이메일 인증 확인
            throw new CustomException(EMAIL_NOT_VERIFIED, "signup");
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword()); // 비밀번호 암호화
        emailVerificationRepository.deleteByEmail(user.getEmail()); // EmailVerificationEntity에 있는 데이터 삭제
        
        accountRepository.save(user.toEntity(encodedPassword)); // 저장
    }
    
    /**
     * 로그인 함수
     * @param user username, password 이 담긴 유저 정보 dto
     * @param session 로그인 했을 때 session에 저장
     */
    public void signIn(SigninDto user, HttpSession session) {

        AccountEntity accountEntity = this.accountRepository.findByUsername(user.getUsername()).orElseThrow(() -> new CustomException(USER_NOT_FOUND,"signin")); // 유저 있는지 확인, 없으면 throw

        if (!passwordEncoder.matches(user.getPassword(), accountEntity.getPassword())) { // 비밀번호 확인
            throw new CustomException(INCORRECT_PASSWORD,"signin");
        }

        String roleName = "ROLE_" + accountEntity.getRole().name().replace("ROLE_", ""); // ROLE_ 를 추가해야 USER로 인식함
        UserDetails userDetails = new User( // Spring Security에 저장 + user 역할 부여
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
    }
    
    /**
     * 비밀번호 변경 함수
     * @param toChangePassword 기존 비밀번호와 새로 변경할 비밀번호 정보가 담긴 dto
     * @param username 비밀번호 바꿀 유저 이름
     */
    public void changePassword(ChangePasswordDto toChangePassword, String username) {
        AccountEntity accountEntity = this.accountRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND, "change-info")); // 유저 있는지 확인, 없으면 exception throw

        if (!passwordEncoder.matches(toChangePassword.getCurrentPassword(), accountEntity.getPassword())) { // 비밀번호 확인, 틀리면 exception throw
            throw new CustomException(INCORRECT_PASSWORD,"change-info");
        }

        String encodedPassword = passwordEncoder.encode(toChangePassword.getNewPassword()); // 비밀번호 encode
        accountEntity.setPassword(encodedPassword); // encode 한 비밀번호로 변경
        this.accountRepository.save(accountEntity); // account entity 저장
    }

    /**
     * 계정 삭제 함수
     * @param toDeleteAccount 현재재 비밀번호와 재확인을 위해 다시 입력한 비밀번호 정보가 담긴 dto
     * @param username 비밀번호 바꿀 유저 이름
     */
    public void deleteAccount(DeleteAccountDto toDeleteAccount, String username) {
        AccountEntity accountEntity = this.accountRepository.findByUsername(username).orElseThrow(() -> new CustomException(USER_NOT_FOUND, "change-info")); // 유저 있는지 확인, 없으면 exception throw

        if (!passwordEncoder.matches(toDeleteAccount.getCurrentPassword(), accountEntity.getPassword())) { // 비밀번호 확인, 틀리면 exception throw
            throw new CustomException(INCORRECT_PASSWORD,"deleteAccountPage");
        }
        if (!toDeleteAccount.getCurrentPassword().equals(toDeleteAccount.getConfirmPassword())) { // 비밀번호와 재확인 비밀번호가 맞는지 확인, 틀리면 exception throw
            throw new CustomException(PASSWORD_DOES_NOT_MATCH,"deleteAccountPage");
        }

        this.accountRepository.delete(accountEntity); // 회원탈퇴
    }
}