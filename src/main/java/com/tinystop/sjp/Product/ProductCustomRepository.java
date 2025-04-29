package com.tinystop.sjp.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tinystop.sjp.Type.ProductCategory;

public interface ProductCustomRepository {
    Page<ProductEntity> findByNameContainingOrderByModifiedAtDesc(String name, Pageable pageable);
    Page<ProductEntity> findProductsByComponentOrderByModifiedAtDesc(ProductCategory category, Pageable pageable);
}
