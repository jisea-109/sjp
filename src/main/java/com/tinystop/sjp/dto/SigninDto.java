package com.tinystop.sjp.dto;

import com.tinystop.sjp.entity.AccountEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SigninDto {
    private String username;
    private String password;
    
    @Getter
    @Builder
    public static class Response {
        private String username;
        private String email;
        
        public static Response from(AccountEntity accountEntity) {
            return Response.builder()
            .username(accountEntity.getUsername())
            .email(accountEntity.getEmail())
            .build();
        }
    }
}
