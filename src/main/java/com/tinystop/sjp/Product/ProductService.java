package com.tinystop.sjp.Product;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tinystop.sjp.Type.ProductCategory;
import com.tinystop.sjp.Exception.CustomException;

import static com.tinystop.sjp.Type.ErrorCode.PRODUCT_NOT_FOUND;

@Transactional
@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    
    public Page<LoadProductDto> getProductsByName(String productName, Pageable pageable) {
        Page<ProductEntity> productList = productRepository.findAllByNameContaining(productName, pageable);

        if (productName == "" || productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND,"main");
        }
        
        return productList.map(LoadProductDto::new);
    }

    public Page<LoadProductDto> getProductsByNameOrderByDate(String name, Pageable pageable) {
        Page<ProductEntity> productList = productRepository.findProductsByNameSortedByModifiedAtDesc(name, pageable);

        if (productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND, "main");
        }

        return productList.map(LoadProductDto::new);
    }

    public Page<LoadProductDto> getProductsByComponent(String component, Pageable pageable) {

        ProductCategory category = ProductCategory.valueOf(component.toUpperCase());
        Page<ProductEntity> productList = productRepository.findAllByComponent(category, pageable);
        
        if (productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND,"main");
        }
        return productList.map(LoadProductDto::new);
    }

    public Page<LoadProductDto> getProductsByComponentOrderByDate(String component, Pageable pageable) {
        ProductCategory category = ProductCategory.valueOf(component.toUpperCase());
        Page<ProductEntity> productList = productRepository.findProductsByComponentSortedByModifiedAtDesc(category, pageable);
        
        if (productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND,"main");
        }
        return productList.map(LoadProductDto::new);
    }

    public Page<LoadProductDto> getProductsByNameOrderBySales(String name, Pageable pageable) {
        Page<ProductEntity> productList = productRepository.searchProductsSortedBySales(name, pageable);

        if (productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND, "main");
        }

        return productList.map(LoadProductDto::new);
    }

    public Page<LoadProductDto> getProductsByComponentOrderBySales(String component, Pageable pageable) {
        ProductCategory category = ProductCategory.valueOf(component.toUpperCase());
        Page<ProductEntity> productList = productRepository.searchProductComponentsSortedBySales(category, pageable);
        
        if (productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND,"main");
        }

        return productList.map(LoadProductDto::new);
    }

    public Page<LoadProductDto> getProductsByNameOrderByReviews(String name, Pageable pageable) {
        Page<ProductEntity> productList = productRepository.searchProductSortedByReviews(name, pageable);

        if (productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND, "main");
        }

        return productList.map(LoadProductDto::new);
    }

    public Page<LoadProductDto> getProductsByComponentOrderByReviews(String component, Pageable pageable) {
        ProductCategory category = ProductCategory.valueOf(component.toUpperCase());
        Page<ProductEntity> productList = productRepository.searchProductComponentsSortedByReviews(category, pageable);
        
        if (productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND,"main");
        }

        return productList.map(LoadProductDto::new);
    }

    public ProductEntity getProduct(Long productId) {
        ProductEntity product = productRepository.findById(productId).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND,"main"));
        return product;
    }
}
