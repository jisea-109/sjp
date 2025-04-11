package com.tinystop.sjp.Auth.AuthDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteAccountDto {
    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String currentPassword;

    @NotBlank(message = "다시 비밀번호를 입력해주세요.")
    private String confirmPassword;
}
