package com.tinystop.sjp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tinystop.sjp.entity.ProductEntity;

public interface AdminRepository extends JpaRepository<ProductEntity, Long> {
    boolean existsByName(String productName);
}
