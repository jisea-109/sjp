package com.tinystop.sjp.Admin;

import com.tinystop.sjp.Product.ProductEntity;
import com.tinystop.sjp.type.ProductCategory;

import lombok.Getter;
import lombok.Setter;

public class ManageProductDto {
    @Getter
    @Setter
    public static class ManageProduct {
        private Long id;
        private String name;
        private int price;
        private ProductCategory component;
        private String socket;

        public ManageProduct() {}

        public ProductEntity toEntity() {
            return ProductEntity.builder()
                .id(this.id)
                .name(this.name)
                .price(this.price)
                .component(this.component)
                .socket(this.socket)
                .build();
        }
    }
}
