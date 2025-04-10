package com.tinystop.sjp.Product;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tinystop.sjp.Exception.CustomException;

import lombok.RequiredArgsConstructor;

import static com.tinystop.sjp.type.ErrorCode.PRODUCT_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductEntity> GetProducts(String productName) {
        List<ProductEntity> productList = productRepository.findAllByNameContaining(productName);

        if (productName != "" && productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND,"product-list");
        }

        return productList;
    }
}
