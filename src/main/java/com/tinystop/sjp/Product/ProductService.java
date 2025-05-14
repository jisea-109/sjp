package com.tinystop.sjp.Product;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tinystop.sjp.Exception.CustomException;
import com.tinystop.sjp.Product.Category.ProductCategoryEntity;
import com.tinystop.sjp.Product.Category.ProductCategoryRepository;

import static com.tinystop.sjp.Type.ErrorCode.PRODUCT_NOT_FOUND;

@Transactional
@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    /**
     * 클라이언트에서 product를 검색할 때 제일 최우선으로 사용되는 함수, 대소문자 구분 없이 검색함.
     * @param name 검색할 product 이름
     * @param pageable 페이지 번호, 크기, 정보 담은 객체
     * @return DB에서 찾은 product entity -> LoadProductDto으로 return (imagePaths의 Hibernate Lazy 로딩 방지)
     */
    public Page<LoadProductDto> getProductsByName(String name, Pageable pageable) {
        Page<ProductEntity> productList = productRepository.searchProductByNameContaining(name, pageable);

        if (name == "" || productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND,"main");
        }
        
        return productList.map(LoadProductDto::new);
    }
    /**
     * 특정 Component Product를 정렬하는 함수, Component에 속하는 Product만 정렬함. (CPU, Mainboard 등)
     * @param component Component 이름 (ProductCategory에 있는 Component 변수랑 똑같이 일치해야함)
     * @param pageable 페이지 번호, 크기, 정보 담은 객체
     * @return DB에서 찾은 product entity -> LoadProductDto으로 return (imagePaths의 Hibernate Lazy 로딩 방지)
     */
    public Page<LoadProductDto> getProductsByComponent(String component, Pageable pageable) {

        ProductCategoryEntity category = productCategoryRepository.findByName(component);
        Page<ProductEntity> productList = productRepository.findAllByComponent(category, pageable);
        
        if (productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND,"main");
        }
        return productList.map(LoadProductDto::new);
    }

    /**
     * 검색해서 나온 Product 중에서 최신 등록순으로 정렬하는 함수 (CreatedAt 시간 기준) 
     * @param name 검색할 product 이름
     * @param pageable 페이지 번호, 크기, 정보 담은 객체
     * @return DB에서 찾은 product entity -> LoadProductDto으로 return (imagePaths의 Hibernate Lazy 로딩 방지)
     */
    public Page<LoadProductDto> getProductsByNameOrderByDate(String name, Pageable pageable) {
        Page<ProductEntity> productList = productRepository.searchProductsByNameSortedByCreatedAtDesc(name, pageable);

        if (productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND, "main");
        }

        return productList.map(LoadProductDto::new);
    }

    /**
     * 특정 Component Product 중에서 최신 등록순으로 정렬하는 함수 (CreatedAt 시간 기준) 
     * @param component Component 이름 (ProductCategory에 있는 Component 변수랑 똑같이 일치해야함)
     * @param pageable 페이지 번호, 크기, 정보 담은 객체
     * @return DB에서 찾은 product entity -> LoadProductDto으로 return (imagePaths의 Hibernate Lazy 로딩 방지)
     */
    public Page<LoadProductDto> getProductsByComponentOrderByDate(String component, Pageable pageable) {
        ProductCategoryEntity category = productCategoryRepository.findByName(component);
        Page<ProductEntity> productList = productRepository.searchProductsByComponentSortedByCreatedAtDesc(category, pageable);
        
        if (productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND,"main");
        }
        return productList.map(LoadProductDto::new);
    }

    /**
     * 검색해서 나온 Product 중에서 판매량이 제일 많은 순으로 정렬하는 함수 (User가 제일 많이 Order한 Product) 
     * @param name 검색할 product 이름
     * @param pageable 페이지 번호, 크기, 정보 담은 객체
     * @return DB에서 찾은 product entity -> LoadProductDto으로 return (imagePaths의 Hibernate Lazy 로딩 방지)
     */
    public Page<LoadProductDto> getProductsByNameOrderBySales(String name, Pageable pageable) {
        Page<ProductEntity> productList = productRepository.searchProductsSortedBySales(name, pageable);

        if (productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND, "main");
        }

        return productList.map(LoadProductDto::new);
    }

    /**
     * 특정 Component Product 중에서 판매량이 제일 많은 순으로 정렬하는 함수 (User가 제일 많이 Order한 Product)
     * @param component Component 이름 (ProductCategory에 있는 Component 변수랑 똑같이 일치해야함)
     * @param pageable 페이지 번호, 크기, 정보 담은 객체
     * @return DB에서 찾은 product entity -> LoadProductDto으로 return (imagePaths의 Hibernate Lazy 로딩 방지)
     */
    public Page<LoadProductDto> getProductsByComponentOrderBySales(String component, Pageable pageable) {
        ProductCategoryEntity category = productCategoryRepository.findByName(component);
        Page<ProductEntity> productList = productRepository.searchProductComponentsSortedBySales(category, pageable);
        
        if (productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND,"main");
        }

        return productList.map(LoadProductDto::new);
    }

    /**
     * 검색해서 나온 Product 중에서 User 리뷰량이 제일 많은 순으로 정렬하는 함수
     * @param name 검색할 product 이름
     * @param pageable 페이지 번호, 크기, 정보 담은 객체
     * @return DB에서 찾은 product entity -> LoadProductDto으로 return (imagePaths의 Hibernate Lazy 로딩 방지)
     */
    public Page<LoadProductDto> getProductsByNameOrderByReviews(String name, Pageable pageable) {
        Page<ProductEntity> productList = productRepository.searchProductSortedByReviews(name, pageable);

        if (productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND, "main");
        }

        return productList.map(LoadProductDto::new);
    }

    /**
     * 특정 Component Product 중에서 User 리뷰량이 제일 많은 순으로 정렬하는 함수
     * @param component Component 이름 (ProductCategory에 있는 Component 변수랑 똑같이 일치해야함)
     * @param pageable 페이지 번호, 크기, 정보 담은 객체
     * @return DB에서 찾은 product entity -> LoadProductDto으로 return (imagePaths의 Hibernate Lazy 로딩 방지)
     */
    public Page<LoadProductDto> getProductsByComponentOrderByReviews(String component, Pageable pageable) {
        ProductCategoryEntity category = productCategoryRepository.findByName(component);
        Page<ProductEntity> productList = productRepository.searchProductComponentsSortedByReviews(category, pageable);
        
        if (productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND,"main");
        }

        return productList.map(LoadProductDto::new);
    }
    /**
     * 특정 product 정보 가져오는 함수
     * @param name 검색할 product 이름
     * @param pageable 페이지 번호, 크기, 정보 담은 객체
     * @return DB에서 찾은 product entity -> LoadProductDto으로 return (imagePaths의 Hibernate Lazy 로딩 방지)
     */
    public ProductEntity getProduct(Long productId) {
        ProductEntity product = productRepository.findById(productId).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND,"main"));
        return product;
    }
}
