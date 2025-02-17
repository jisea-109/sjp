package com.tinystop.sjp.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Builder
@Getter
@Entity
@Table(name="ACCOUNT_TABLE") 
public class AccountEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ACCOUNT_ID")
    private Long userID;

    @Column(name = "NAME", nullable = false, unique = true, length = 15) // not null, varchar(15)
    private String username;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "EMAIL", nullable = false, length = 30)
    private String email;

    @OneToMany
    private List<OrderEntity> orders;

    @OneToMany
    private List<CartEntity> carts;
 
    protected AccountEntity() {} // 기본 생성자

    // 매개변수 있는 생성자 추가
    public AccountEntity(Long userID, String username, String password, String email,
                         List<OrderEntity> orders, List<CartEntity> carts) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.email = email;
        this.orders = orders;
        this.carts = carts;
    }

    // id를 제외한 생성자
    public AccountEntity(String username, String password, String email,
                         List<OrderEntity> orders, List<CartEntity> carts) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.orders = orders;
        this.carts = carts;
    }

    
}
