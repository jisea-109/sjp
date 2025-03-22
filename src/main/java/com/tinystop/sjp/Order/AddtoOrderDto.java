package com.tinystop.sjp.Order;

import com.tinystop.sjp.Auth.AccountEntity;
import com.tinystop.sjp.Product.ProductEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddtoOrderDto {
    private Long id;
    private Long productId;
    private ProductEntity product;
    private int quantity;

     public OrderEntity toEntity(AccountEntity account, ProductEntity product, int quantity) {
        return OrderEntity.builder()
            .account(account)
            .product(product)
            .quantity(quantity)
            .build();
    }
}
