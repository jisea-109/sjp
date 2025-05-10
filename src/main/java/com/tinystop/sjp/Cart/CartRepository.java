package com.tinystop.sjp.Cart;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tinystop.sjp.Auth.AccountEntity;
import com.tinystop.sjp.Product.ProductEntity;
public interface CartRepository extends JpaRepository<CartEntity, Long> {
    List<CartEntity> findAllByAccount(AccountEntity account); // account에 있는 cart 가져오기
    CartEntity findByAccountAndProduct(AccountEntity account, ProductEntity product); // 특정 유저가 product를 기존에 cart에 담은거 찾기
}
