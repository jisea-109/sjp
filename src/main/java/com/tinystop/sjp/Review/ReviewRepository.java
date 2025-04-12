package com.tinystop.sjp.Review;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tinystop.sjp.Auth.AccountEntity;
import com.tinystop.sjp.Product.ProductEntity;
import java.util.List;


public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    List<ReviewEntity> findByAccount(AccountEntity account);
    List<ReviewEntity> findByProduct(ProductEntity prouct);
    boolean existsByAccountAndProduct(AccountEntity account, ProductEntity product);
    ReviewEntity findByAccountAndId(AccountEntity account, Long id);
    
}
