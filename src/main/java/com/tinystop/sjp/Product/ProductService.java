package com.tinystop.sjp.Product;

import static com.tinystop.sjp.Type.ErrorCode.PRODUCT_NOT_FOUND;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tinystop.sjp.Exception.CustomException;

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
            throw new CustomException(PRODUCT_NOT_FOUND,"product-list");
        }
        for (ProductEntity product : productList) {
            System.out.println("Product: " + product.getName());
            System.out.println("ImagePaths: " + product.getImagePaths());
        }
        return productList;
    }

    public ProductEntity GetProduct(Long productId) {
        ProductEntity product = productRepository.findById(productId).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND,"product-list"));
        return product;
    }
}
