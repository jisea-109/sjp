package com.tinystop.sjp.Cart;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tinystop.sjp.Auth.AccountEntity;
import com.tinystop.sjp.Product.ProductEntity;
public interface CartRepository extends JpaRepository<CartEntity, Long> {
    List<CartEntity> findAllByAccount(AccountEntity account);
    CartEntity findByAccountAndProduct(AccountEntity account, ProductEntity product);
}
