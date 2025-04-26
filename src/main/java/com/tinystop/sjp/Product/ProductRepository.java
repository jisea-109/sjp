package com.tinystop.sjp.Product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.tinystop.sjp.Type.ProductCategory;

public interface ProductRepository extends JpaRepository<ProductEntity, Long>{
    List<ProductEntity> findAllByNameContaining(@Param("name") String productName);
    List<ProductEntity> findAllBySocket(String productName);
    List<ProductEntity> findAllByComponent(ProductCategory component);
    Optional<ProductEntity> findByName(String username);
    Optional<ProductEntity> findById(long id);
}
