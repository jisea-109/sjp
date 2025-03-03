package com.tinystop.sjp.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import com.tinystop.sjp.dto.ManageProductDto.ManageProduct;
import com.tinystop.sjp.entity.ProductEntity;
import com.tinystop.sjp.exception.CustomException;
import com.tinystop.sjp.repository.AdminRepository;
import static com.tinystop.sjp.type.ErrorCode.ALREADY_EXIST_PRODUCT;
import static com.tinystop.sjp.type.ErrorCode.PRODUCT_NOT_FOUND;

@RequiredArgsConstructor
@Transactional
@Service
public class AdminService {
    private final AdminRepository adminRepository;

    
    public ProductEntity AddProduct(ManageProduct product) {
        boolean exists = adminRepository.existsByName(product.getName());
        
        if (exists) { 
            throw new CustomException(ALREADY_EXIST_PRODUCT);
        }

        return this.adminRepository.save(product.toEntity());
    }

    public ProductEntity ModifyProduct(ManageProduct product) {
        ProductEntity toModifyProduct = adminRepository.findById(product.getId()).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        toModifyProduct.setName(product.getName());
        toModifyProduct.setPrice(product.getPrice());
        toModifyProduct.setComponent(product.getComponent());
        toModifyProduct.setSocket(product.getSocket());

        return this.adminRepository.save(toModifyProduct);
    }
    
    public List<ProductEntity> GetProducts(String productName) {
        List<ProductEntity> productList = adminRepository.findAllByNameContaining(productName);

        if (productName != "" && productList.isEmpty()) {
            throw new CustomException(PRODUCT_NOT_FOUND);
        }

        return productList;
    }
    
}
