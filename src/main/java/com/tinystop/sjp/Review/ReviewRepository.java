package com.tinystop.sjp.Review;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tinystop.sjp.Auth.AccountEntity;
import com.tinystop.sjp.Product.ProductEntity;
import java.util.List;


public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    List<ReviewEntity> findByAccount(AccountEntity account);
    boolean existsByAccountAndProduct(AccountEntity account, ProductEntity product);
    ReviewEntity findByAccountAndId(AccountEntity account, Long id);
    ReviewEntity findByAccountAndProduct(AccountEntity account, ProductEntity product);
}
