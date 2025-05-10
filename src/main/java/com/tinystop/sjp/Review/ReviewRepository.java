package com.tinystop.sjp.Review;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tinystop.sjp.Auth.AccountEntity;
import com.tinystop.sjp.Product.ProductEntity;
import java.util.List;


public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    List<ReviewEntity> findByAccount(AccountEntity account); // 특정 유저가 작성한 리뷰 찾기
    List<ReviewEntity> findByProduct(ProductEntity product); // product에 관한 리뷰 찾기
    boolean existsByAccountAndProduct(AccountEntity account, ProductEntity product); // 특정 유저가 특정 product의 리뷰를 작성했는지 체크
}
