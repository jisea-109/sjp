package com.tinystop.sjp.dto;

import com.tinystop.sjp.entity.AccountEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CreateAccountDto {
    @Getter
    @Setter
    @Builder // args 관련 annotation 추가하면 오류걸리는 이유 알아보기
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
        // @Override
        // public String toString() {
        //     return "AccountEntity{" +
        //             "name: " + username + '\'' +
        //             "password: " + password + '\'' +
        //             "email: " + email + '\'' +
        //             '}';
        // }
    }
    @Builder
    @Getter
    public static class SignUpResponse {
        private String username;
        private String password;
        private String email;
    }
}
