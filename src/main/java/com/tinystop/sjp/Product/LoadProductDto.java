package com.tinystop.sjp.Product;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.tinystop.sjp.Product.Category.ProductCategoryEntity;
import com.tinystop.sjp.Type.ProductStockStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoadProductDto {
    
    private Long id;
    private String name;
    private String description;
    private int price;
    private ProductCategoryEntity component;
    private String socket;
    private int quantity;
    private List<String> imagePaths;
    private ProductStockStatus stockStatus;
    private int reviewCount;

    public LoadProductDto(ProductEntity product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.component = product.getComponent();
        this.socket = product.getSocket();
        this.quantity = product.getQuantity();
        this.imagePaths = List.copyOf(product.getImagePaths()); // Lazy 초기화
        this.stockStatus = product.getStockStatus();
        this.reviewCount = product.getReviews() != null ? product.getReviews().size() : 0; // Lazy 초기화
    }
}
