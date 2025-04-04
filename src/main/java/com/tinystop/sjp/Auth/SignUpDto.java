package com.tinystop.sjp.Auth;

import com.tinystop.sjp.type.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpDto {

    @NotBlank(message = "아이디를 입력해주세요.")
    @Pattern
    (
        regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{7,15}$",
        message = "아이디는 영문과 숫자를 포함한 7자 이상, 15자 이하이어야 합니다."
    )
    private String username;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern
    (
        regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=]).{8,20}$",
        message = "비밀번호는 영문, 숫자, 특수문자를 포함한 8자 이상, 20자 이하이어야 합니다."
    )
    private String password;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식이 아니거나 이미 가입된 이메일이 있습니다.")
    private String email;
    
    public AccountEntity toEntity(String encodedPassword) {
        return AccountEntity.builder()
            .username(this.username)
            .password(encodedPassword)
            .email(this.email)
            .role(Role.USER)
            .build();
    }
}
