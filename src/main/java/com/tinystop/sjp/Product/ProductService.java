package com.tinystop.sjp.Product;

import java.util.List;
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
    
    public Page<ProductEntity> GetProductsByName(String productName, Pageable pageable) {
        Page<ProductEntity> productList = productRepository.findAllByNameContaining(productName, pageable);

        if (productName == "" || productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND,"main");
        }

        for (ProductEntity product : productList) {
            product.getImagePaths().size();  // Hibernate Lazy 방지
        }
        
        return productList;
    }

    public Page<ProductEntity> GetProductsByNameOrderByDate(String name, Pageable pageable) {
        Page<ProductEntity> productList = productRepository.findByNameContainingOrderByModifiedAtDesc(name, pageable);

        if (productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND, "main");
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

    public Page<ProductEntity> GetProductsByComponent(String component, Pageable pageable) {

        ProductCategory category = ProductCategory.valueOf(component.toUpperCase());
        Page<ProductEntity> productList = productRepository.findAllByComponent(category, pageable);

        for (ProductEntity product : productList) {
            product.getImagePaths().size(); // Hibernate Lazy 방지
        }
        
        if (productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND,"main");
        }
        return productList;
    }

    public Page<ProductEntity> GetProductsByComponentOrderByDate(String component, Pageable pageable) {
        ProductCategory category = ProductCategory.valueOf(component.toUpperCase());
        Page<ProductEntity> productList = productRepository.findProductsByComponentOrderByModifiedAtDesc(category, pageable);

        for (ProductEntity product : productList) {
            product.getImagePaths().size(); // Hibernate Lazy 방지
        }
        
        if (productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND,"main");
        }
        return productList;
    }
}
