package com.tinystop.sjp.Auth.Admin;

import com.tinystop.sjp.Product.ProductEntity;
import com.tinystop.sjp.type.ProductCategory;
import com.tinystop.sjp.type.ProductStockStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManageProductDto {

    private Long id;
    private String name;
    private int price;
    private ProductCategory component;
    private String socket;
    private int quantity;
    private ProductStockStatus stockStatus;

    public ManageProductDto() {}

    public ProductEntity toEntity() {
        if (this.quantity > 0) { stockStatus = ProductStockStatus.IN_STOCK; }
        else { stockStatus = ProductStockStatus.SOLD_OUT; }
        
        return ProductEntity.builder()
            .id(this.id)
            .name(this.name)
            .price(this.price)
            .component(this.component)
            .socket(this.socket)
            .quantity(this.quantity)
            .stockStatus(this.stockStatus)
            .build();
    }
}

