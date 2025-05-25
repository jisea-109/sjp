package com.tinystop.sjp.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tinystop.sjp.Product.Category.ProductCategoryEntity;

public interface ProductCustomRepository {

    /**
     * 클라이언트에서 product를 검색할 때 제일 최우선으로 사용되는 함수, 대소문자 구분 없이 검색함.
     *  정확도 정렬 기준 (내림차순):
     *  - 이름이 완전히 같은 경우 → 점수 3
     *  - 이름이 입력값으로 시작하는 경우 → 점수 2
     *  - 이름에 입력값이 포함된 경우 → 점수 1
     *  - 그 외 → 점수 0
     * @param name 검색할 product 이름
     * @param pageable 페이지 번호, 크기, 정보 담은 객체
     * @return 기준에 맞는 ProductEntity Page return
     */
    Page<ProductEntity> searchProductByNameContaining(String name, Pageable pageable);

    /**
     *  특정 Component에 해당하는 Product 검색해서 판매량과 리뷰량을 계산한 인기순으로 정렬하는 함수 
     * @param category Component 이름 (ProductCategory에 있는 Component 변수랑 똑같이 일치해야함)
     * @param pageable 페이지 번호, 크기, 정보 담은 객체
     * @return 기준에 맞는 ProductEntity Page return
     */
    Page<ProductEntity> searchProductByComponent(ProductCategoryEntity component, Pageable pageable);

    /**
     * 검색해서 나온 Product 중에서 최신 등록순으로 정렬하는 함수 (CreatedAt 시간 기준) 
     * @param name 검색할 product 이름
     * @param pageable 페이지 번호, 크기, 정보 담은 객체
     * @return 기준에 맞는 ProductEntity Page return
     */
    Page<ProductEntity> searchProductsByNameSortedByCreatedAtDesc(String name, Pageable pageable);

    /**
     * 특정 Component Product 중에서 최신 등록순으로 정렬하는 함수 (CreatedAt 시간 기준) 
     * @param component Component 이름 (ProductCategory에 있는 Component 변수랑 똑같이 일치해야함)
     * @param pageable 페이지 번호, 크기, 정보 담은 객체
     * @return 기준에 맞는 ProductEntity Page return
     */
    Page<ProductEntity> searchProductsByComponentSortedByCreatedAtDesc(ProductCategoryEntity component, Pageable pageable);

    /**
     * 검색해서 나온 Product 중에서 판매량이 제일 많은 순으로 정렬하는 함수 (User가 제일 많이 Order한 Product) 
     * @param name 검색할 product 이름
     * @param pageable 페이지 번호, 크기, 정보 담은 객체
     * @return 기준에 맞는 ProductEntity Page return
     */
    Page<ProductEntity> searchProductsSortedBySales(String name, Pageable pageable);
    
    /**
     * 특정 Component Product 중에서 판매량이 제일 많은 순으로 정렬하는 함수 (User가 제일 많이 Order한 Product)
     * @param component Component 이름 (ProductCategory에 있는 Component 변수랑 똑같이 일치해야함)
     * @param pageable 페이지 번호, 크기, 정보 담은 객체
     * @return 기준에 맞는 ProductEntity Page return
     */
    Page<ProductEntity> searchProductComponentsSortedBySales(ProductCategoryEntity component, Pageable pageable);

    /**
     * 검색해서 나온 Product 중에서 User 리뷰량이 제일 많은 순으로 정렬하는 함수
     * @param name 검색할 product 이름
     * @param pageable 페이지 번호, 크기, 정보 담은 객체
     * @return 기준에 맞는 ProductEntity Page return
     */
    Page<ProductEntity> searchProductSortedByReviews(String name, Pageable pageable);
    
    /**
     * 특정 Component Product 중에서 User 리뷰량이 제일 많은 순으로 정렬하는 함수
     * @param component Component 이름 (ProductCategory에 있는 Component 변수랑 똑같이 일치해야함)
     * @param pageable 페이지 번호, 크기, 정보 담은 객체
     * @return 기준에 맞는 ProductEntity Page return
     */
    Page<ProductEntity> searchProductComponentsSortedByReviews(ProductCategoryEntity component, Pageable pageable);
}
