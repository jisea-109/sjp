package com.tinystop.sjp.dto;

import com.tinystop.sjp.entity.AccountEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class CreateAccountDto {
    @Getter
    @Setter
    @Builder
    public static class SignUp {
        private String username;
        private String password;
        private String email;
        
        public AccountEntity toEntity() {
            return AccountEntity.builder()
                .username(this.username)
                .password(this.password)
                .email(this.email)
                .build();
        }
    }
}
