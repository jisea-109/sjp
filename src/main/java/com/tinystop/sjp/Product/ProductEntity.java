package com.tinystop.sjp.Product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.tinystop.sjp.BaseEntity;
import com.tinystop.sjp.type.ProductCategory;
import com.tinystop.sjp.type.ProductStockStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@EntityListeners(AuditingEntityListener.class)
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

    @Enumerated(EnumType.STRING)
    @Column(name = "COMPONENT", nullable = false)
    private ProductCategory component;

    @Column(name = "SOCKET")
    private String socket;

    @Column(name = "Quantity", nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "STOCK", nullable = false)
    private ProductStockStatus stockStatus;

}
