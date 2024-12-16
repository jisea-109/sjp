package com.tinystop.sjp.entity;

import org.springframework.data.annotation.Id;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
// import jakarta.validation.constraints.Min; @Min(1)
// import jakarta.validation.constraints.NotNull; @NotNull
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@ToString
@Builder
@Setter
@Getter
@Entity(name = "ACCOUNT")
@Table(name="ACCOUNT_TABLE")
public class AccountEntity extends BaseEntity {

    @Id // primary key
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long userID;

    @Column(name = "NAME", nullable = false, length = 15) // not null, varchar(15)
    private String username;

    @Column(name = "PASSWORD", nullable = false, length = 20)
    private String password;

    @Column(name = "EAMIL", length = 30)
    private String email;

}
