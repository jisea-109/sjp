package com.tinystop.sjp.Cart;

import com.tinystop.sjp.Product.ProductEntity;
import com.tinystop.sjp.Auth.AccountEntity;
import com.tinystop.sjp.type.OrderableStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToCartDto {
    private Long id;
    private Long productId;
    private ProductEntity product;
    private int quantity;

    public CartEntity toEntity(AccountEntity account, ProductEntity product, int quantity) {
        return CartEntity.builder()
            .account(account)
            .product(product)
            .quantity(quantity)
            .status(OrderableStatus.IN_STOCK)
            .build();
    }
}
