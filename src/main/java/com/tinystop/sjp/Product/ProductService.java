package com.tinystop.sjp.Product;

import static com.tinystop.sjp.Type.ErrorCode.PRODUCT_NOT_FOUND;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tinystop.sjp.Exception.CustomException;
import com.tinystop.sjp.Type.ProductCategory;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductEntity> GetProducts(String productName) {
        List<ProductEntity> productList = productRepository.findAllByNameContaining(productName);

        if (productName != "" && productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND,"main");
        }

        for (ProductEntity product : productList) {
            product.getImagePaths().size();  // Hibernate Lazy 방지
        }
        
        return productList;
    }

    public ProductEntity GetProduct(Long productId) {
        ProductEntity product = productRepository.findById(productId).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND,"main"));
        return product;
    }

    public List<ProductEntity> GetProductsByComponent(String component) {
        ProductCategory category = ProductCategory.valueOf(component.toUpperCase());
        List<ProductEntity> productList = productRepository.findAllByComponent(category);

        for (ProductEntity product : productList) {
            product.getImagePaths().size(); // Hibernate Lazy 방지
        }
        
        if (productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND,"main");
        }
        return productList;
    }
}
