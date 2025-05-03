package com.tinystop.sjp.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tinystop.sjp.Type.ProductCategory;

public interface ProductCustomRepository {
    Page<ProductEntity> findProductsByNameSortedByModifiedAtDesc(String name, Pageable pageable);
    Page<ProductEntity> findProductsByComponentSortedByModifiedAtDesc(ProductCategory category, Pageable pageable);
    Page<ProductEntity> searchProductsSortedBySales(String name, Pageable pageable);
    Page<ProductEntity> searchProductComponentsSortedBySales(ProductCategory component, Pageable pageable);
    Page<ProductEntity> searchProductSortedByReviews(String name, Pageable pageable);
    Page<ProductEntity> searchProductComponentsSortedByReviews(ProductCategory component, Pageable pageable);
}
