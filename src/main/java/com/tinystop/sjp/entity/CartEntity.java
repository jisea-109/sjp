package com.tinystop.sjp.entity;

import com.tinystop.sjp.entity.AccountEntity;
import com.tinystop.sjp.entity.ProductEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Entity
@Table(name = "CART_TABLE")
public class CartEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "CART_ID")
    private long id;

    @ManyToOne
    @JoinColumn(name="ACCOUNT_ID", nullable = false)
    private AccountEntity accountID;

    @ManyToOne
    @JoinColumn(name="ID", nullable = false)
    private ProductEntity productID;
}
