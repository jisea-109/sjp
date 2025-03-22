package com.tinystop.sjp.Order;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tinystop.sjp.Auth.AccountEntity;
import com.tinystop.sjp.Product.ProductEntity;
@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findAllByAccount(AccountEntity user);
    //OrderEntity findByAccountAndProduct(AccountEntity user, ProductEntity product);
    boolean existsByIdAndAccountAndProduct(Long orderId, AccountEntity user, ProductEntity product);
}
