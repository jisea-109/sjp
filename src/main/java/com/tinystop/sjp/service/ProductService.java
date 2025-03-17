package com.tinystop.sjp.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import com.tinystop.sjp.entity.ProductEntity;
import com.tinystop.sjp.exception.CustomException;
import com.tinystop.sjp.repository.ProductRepository;
import static com.tinystop.sjp.type.ErrorCode.PRODUCT_NOT_FOUND;

@RequiredArgsConstructor
@Transactional
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductEntity> GetProducts(String productName) {
        List<ProductEntity> productList = productRepository.findAllByNameContaining(productName);

        if (productName != "" && productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND);
        }

        return productList;
    }
}
