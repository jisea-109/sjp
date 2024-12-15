package com.tinystop.sjp.entity;

import org.springframework.data.annotation.Id;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
public class AccountEntity {

    @NotNull
    @Id
    private Long userID;
    @NotNull
    @Min(1)
    private String username;
    @NotNull
    @Min(8)
    private String password;
    private String email;

    
    @Builder
    public AccountEntity(String username, String password, String email)
    {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
