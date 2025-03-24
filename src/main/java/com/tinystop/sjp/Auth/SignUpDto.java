package com.tinystop.sjp.Auth;

import com.tinystop.sjp.type.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpDto {

    @NotBlank(message = "아이디는 필수입니다")
    @Size(min = 6, message = "아이디는 6자 이상, 15자 이하이어야 합니다")
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, message = "비밀번호는 8자 이상, 20자 이하이어야 합니다")
    private String password;

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
