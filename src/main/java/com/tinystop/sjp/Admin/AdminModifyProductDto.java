package com.tinystop.sjp.Admin;

import lombok.Setter;

import com.tinystop.sjp.Product.ProductEntity;
import com.tinystop.sjp.type.ProductCategory;
import lombok.Getter;

@Getter
@Setter
public class AdminModifyProductDto {
    private Long id;
    private String name;
    private int price;
    private ProductCategory component;
    private String socket;
    private int quantity;

    public static AdminModifyProductDto from(ProductEntity product) {
        AdminModifyProductDto dto = new AdminModifyProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setComponent(product.getComponent());
        dto.setSocket(product.getSocket());
        dto.setQuantity(product.getQuantity());
        return dto;
    }
}
