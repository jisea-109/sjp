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
    
    public Page<ProductEntity> getProductsByName(String productName, Pageable pageable) {
        Page<ProductEntity> productList = productRepository.findAllByNameContaining(productName, pageable);

        if (productName == "" || productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND,"main");
        }
        
        return productList;
    }

    public Page<ProductEntity> getProductsByNameOrderByDate(String name, Pageable pageable) {
        Page<ProductEntity> productList = productRepository.findProductsByNameSortedByModifiedAtDesc(name, pageable);

        if (productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND, "main");
        }

        return productList;
    }

    public Page<ProductEntity> getProductsByComponent(String component, Pageable pageable) {

        ProductCategory category = ProductCategory.valueOf(component.toUpperCase());
        Page<ProductEntity> productList = productRepository.findAllByComponent(category, pageable);
        
        if (productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND,"main");
        }
        return productList;
    }

    public Page<ProductEntity> getProductsByComponentOrderByDate(String component, Pageable pageable) {
        ProductCategory category = ProductCategory.valueOf(component.toUpperCase());
        Page<ProductEntity> productList = productRepository.findProductsByComponentSortedByModifiedAtDesc(category, pageable);
        
        if (productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND,"main");
        }
        return productList;
    }

    public Page<ProductEntity> getProductsByNameOrderBySales(String name, Pageable pageable) {
        Page<ProductEntity> productList = productRepository.searchProductsSortedBySales(name, pageable);

        if (productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND, "main");
        }

        return productList;
    }

    public Page<ProductEntity> getProductsByComponentOrderBySales(String component, Pageable pageable) {
        ProductCategory category = ProductCategory.valueOf(component.toUpperCase());
        Page<ProductEntity> productList = productRepository.searchProductComponentsSortedBySales(category, pageable);
        
        if (productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND,"main");
        }

        for (ProductEntity product : productList) {
            product.getImagePaths().size();  // Hibernate Lazy 방지
        }

        return productList;
    }

    public Page<ProductEntity> getProductsByNameOrderByReviews(String name, Pageable pageable) {
        Page<ProductEntity> productList = productRepository.searchProductSortedByReviews(name, pageable);

        if (productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND, "main");
        }
        for (ProductEntity product : productList) {
            product.getImagePaths().size();  // Hibernate Lazy 방지
        }

        return productList;
    }

    public ProductEntity getProduct(Long productId) {
        ProductEntity product = productRepository.findById(productId).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND,"main"));
        return product;
    }
}
