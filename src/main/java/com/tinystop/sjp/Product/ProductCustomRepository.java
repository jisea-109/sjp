package com.tinystop.sjp.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

import com.tinystop.sjp.Type.ProductCategory;

public interface ProductCustomRepository {
    @EntityGraph(attributePaths = "imagePaths")
    Page<ProductEntity> findProductsByNameSortedByModifiedAtDesc(String name, Pageable pageable);

    @EntityGraph(attributePaths = "imagePaths")
    Page<ProductEntity> findProductsByComponentSortedByModifiedAtDesc(ProductCategory category, Pageable pageable);
    
    @EntityGraph(attributePaths = "imagePaths")
    Page<ProductEntity> searchProductsSortedBySales(String name, Pageable pageable);

    @EntityGraph(attributePaths = "imagePaths")
    Page<ProductEntity> searchProductComponentsSortedBySales(ProductCategory component, Pageable pageable);

    @EntityGraph(attributePaths = "imagePaths")
    Page<ProductEntity> searchProductSortedByReviews(String name, Pageable pageable);
}
