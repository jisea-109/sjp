package com.tinystop.sjp.Auth;

import com.tinystop.sjp.type.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpDto {
    private String username;
    private String password;
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
