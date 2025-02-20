package com.tinystop.sjp.dto;

import com.tinystop.sjp.entity.AccountEntity;

import lombok.Getter;
import lombok.Setter;

public class CreateAccountDto {
    @Getter
    @Setter
    public static class SignUp {
        private String username;
        private String password;
        private String email;
        
        public SignUp() {}

        public AccountEntity toEntity() {
            return AccountEntity.builder()
                .username(this.username)
                .password(this.password)
                .email(this.email)
                .build();
        }
        public AccountEntity toEntity(String encodedPassword) {
            return AccountEntity.builder()
                .username(this.username)
                .password(encodedPassword)
                .email(this.email)
                .build();
        }
    }
}
