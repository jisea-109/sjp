package com.tinystop.sjp.Auth.Admin;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tinystop.sjp.Product.ProductEntity;

public interface AdminRepository extends JpaRepository<ProductEntity, Long> {
    boolean existsByName(String productName);
}
