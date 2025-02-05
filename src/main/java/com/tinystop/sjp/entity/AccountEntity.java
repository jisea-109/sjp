package com.tinystop.sjp.entity;

import java.util.List;
import com.tinystop.sjp.entity.OrderEntity;
import com.tinystop.sjp.entity.CartEntity;

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

    @Column(name = "NAME", nullable = false, length = 15) // not null, varchar(15)
    private String username;

    @Column(name = "PASSWORD", nullable = false, length = 20)
    private String password;

    @Column(name = "EMAIL", nullable = false, length = 30)
    private String email;

    @OneToMany
    private List<OrderEntity> orders;

    @OneToMany
    private List<CartEntity> carts;
    
}
