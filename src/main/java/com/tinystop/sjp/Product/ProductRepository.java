package com.tinystop.sjp.Product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<ProductEntity, Long>{
    List<ProductEntity> findAllByNameContaining(@Param("name") String productName);
    Optional<ProductEntity> findByName(String username);
    Optional<ProductEntity> findById(long id);
}
