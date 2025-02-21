package com.tinystop.sjp.entity;

import java.util.List;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.tinystop.sjp.type.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@EntityListeners(AuditingEntityListener.class) // createdAt modifiedAt 자동 업데이트
@Table(name="ACCOUNT_TABLE") 
public class AccountEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ACCOUNT_ID")
    private Long userID;

    @Column(name = "USERNAME", nullable = false, unique = true, length = 15) // not null, varchar(15)
    private String username;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 30)
    private String email;

    @Enumerated(EnumType.STRING) 
    @Column(name = "ROLE", nullable = false)
    private Role role = Role.USER;

    @OneToMany
    private List<OrderEntity> orders;

    @OneToMany
    private List<CartEntity> carts;
 
    protected AccountEntity() {} // 기본 생성자

    // 매개변수 있는 생성자 추가
    public AccountEntity(Long userID, String username, String password, String email,
                        Role role, List<OrderEntity> orders, List<CartEntity> carts) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.orders = orders;
        this.carts = carts;
    }

    // id를 제외한 생성자
    public AccountEntity(String username, String password, String email, Role role,
                         List<OrderEntity> orders, List<CartEntity> carts) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.orders = orders;
        this.carts = carts;
    }

    
}
