package com.tinystop.sjp.entity;

import lombok.Getter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;

@Getter
@Builder
@Entity
@Table(name="PRODUCT_TABLE")
public class ProductEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "PRODUCT_ID")
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "PRICE", nullable = false)
    private int price;

    @Column(name = "COMPONENT", nullable = false)
    private String component;

    @Column(name = "SOCKET")
    private String socket;
    
}
