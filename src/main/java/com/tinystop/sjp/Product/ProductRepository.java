package com.tinystop.sjp.Product;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepository extends JpaRepository<ProductEntity, Long>, ProductCustomRepository{
    Optional<ProductEntity> findById(long id);
}
