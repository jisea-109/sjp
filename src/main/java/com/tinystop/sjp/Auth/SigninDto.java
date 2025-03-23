package com.tinystop.sjp.Auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SigninDto {
    @NotBlank(message = "아이디는 필수입니다")
    @Size(min = 6, message = "아이디는 6자 이상, 15자 이하이어야 합니다")
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, message = "비밀번호는 8자 이상, 20자 이하이어야 합니다")
    private String password;
}
