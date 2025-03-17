package com.tinystop.sjp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.tinystop.sjp.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long>{
    List<ProductEntity> findAllByNameContaining(@Param("name") String productName);
}
