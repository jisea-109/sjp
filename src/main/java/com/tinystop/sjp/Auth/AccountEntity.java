package com.tinystop.sjp.Auth;

import java.util.List;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.tinystop.sjp.BaseEntity;
import com.tinystop.sjp.Cart.CartEntity;
import com.tinystop.sjp.Order.OrderEntity;
import com.tinystop.sjp.Review.ReviewEntity;
import com.tinystop.sjp.Type.Role;

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
import jakarta.persistence.CascadeType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@EntityListeners(AuditingEntityListener.class) // createdAt modifiedAt 자동 업데이트
@Table(name="account_table") 
public class AccountEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long userID;

    @Column(name = "username", nullable = false, unique = true, length = 15) // not null, varchar(15)
    private String username;

    @Column(name = "password", nullable = false, length = 60) // Bcrypt로 암호화하는데 암호화 길이는 항상 60자임.
    private String password;

    @Column(name = "email", nullable = false, unique = true, length = 30)
    private String email;

    @Enumerated(EnumType.STRING) 
    @Column(name = "role", nullable = false)
    private Role role = Role.USER;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewEntity> reviews;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderEntity> orders;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartEntity> carts;

    public void setPassword(String password) {
        this.password = password;
    }
}
