package com.tinystop.sjp.Product;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tinystop.sjp.Type.ProductCategory;

public interface ProductRepository extends JpaRepository<ProductEntity, Long>, ProductCustomRepository{
    Page<ProductEntity> findAllByComponent(ProductCategory component, Pageable pageable);
    Optional<ProductEntity> findById(long id);
}
