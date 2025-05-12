package com.tinystop.sjp.Product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.tinystop.sjp.BaseEntity;
import com.tinystop.sjp.Review.ReviewEntity;
import com.tinystop.sjp.Type.ProductCategory;
import com.tinystop.sjp.Type.ProductStockStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="productTable")
public class ProductEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "productId")
    private Long id;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name= "description", nullable = false, length = 300)
    private String description;
    
    @Column(name = "price", nullable = false)
    private int price;

    @Enumerated(EnumType.STRING)
    @Column(name = "component", nullable = false)
    private ProductCategory component;

    @Column(name = "socket")
    private String socket;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @ElementCollection
    @CollectionTable(name = "productImages", joinColumns = @JoinColumn(name = "productId"))
    @Column(name = "imagePath")
    private List<String> imagePaths = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewEntity> reviews;

    @Enumerated(EnumType.STRING)
    @Column(name = "stock", nullable = false)
    private ProductStockStatus stockStatus;

}
