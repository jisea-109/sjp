package com.tinystop.sjp.Product.Category;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<ProductCategoryEntity, Long>{
    boolean existsByName(String categoryName);
    ProductCategoryEntity findByName(String categoryName);
}
