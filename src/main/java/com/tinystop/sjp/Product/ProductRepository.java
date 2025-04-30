package com.tinystop.sjp.Product;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tinystop.sjp.Type.ProductCategory;

public interface ProductRepository extends JpaRepository<ProductEntity, Long>, ProductCustomRepository{
    @EntityGraph(attributePaths = "imagePaths")
    Page<ProductEntity> findAllByNameContaining(String productName, Pageable pageable);

    @EntityGraph(attributePaths = "imagePaths")
    Page<ProductEntity> findAllByComponent(ProductCategory component, Pageable pageable);
    
    Optional<ProductEntity> findByName(String username);
    Optional<ProductEntity> findById(long id);
}
