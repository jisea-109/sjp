package com.tinystop.sjp.dto;

import com.tinystop.sjp.entity.AccountEntity;
import com.tinystop.sjp.type.Role;
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
        
        public AccountEntity toEntity(String encodedPassword) {
            return AccountEntity.builder()
                .username(this.username)
                .password(encodedPassword)
                .email(this.email)
                .role(Role.USER)
                .build();
        }
    }
}
