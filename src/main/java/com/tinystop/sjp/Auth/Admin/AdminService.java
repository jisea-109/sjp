package com.tinystop.sjp.Auth.Admin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import com.tinystop.sjp.Auth.Admin.ManageProductDto;
import com.tinystop.sjp.Auth.Admin.ModifyProductDto;
import com.tinystop.sjp.Exception.CustomException;
import com.tinystop.sjp.Product.ProductEntity;
import com.tinystop.sjp.type.ProductStockStatus;

import static com.tinystop.sjp.type.ErrorCode.ALREADY_EXIST_PRODUCT;
import static com.tinystop.sjp.type.ErrorCode.PRODUCT_NOT_FOUND;

@RequiredArgsConstructor
@Transactional
@Service
public class AdminService {
    
    private final AdminRepository adminRepository;

    public ProductEntity AddProduct(ManageProductDto product) {
        boolean exists = adminRepository.existsByName(product.getName());
        
        if (exists) { 
            throw new CustomException(ALREADY_EXIST_PRODUCT,"admin");
        }
        
        return this.adminRepository.save(product.toEntity());
    }
    public ProductEntity GetProductById(Long id) {
        return adminRepository.findById(id).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND,"product-list"));
    }
    public ProductEntity UpdateProduct(ModifyProductDto product) {
        ProductEntity toUpdateProduct = adminRepository.findById(product.getId()).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND,"product-list"));

        toUpdateProduct.setName(product.getName());
        toUpdateProduct.setPrice(product.getPrice());
        toUpdateProduct.setComponent(product.getComponent());
        toUpdateProduct.setSocket(product.getSocket());
        toUpdateProduct.setQuantity(product.getQuantity());
        if(product.getQuantity() > 0) {
            toUpdateProduct.setStockStatus(ProductStockStatus.IN_STOCK);
        }
        else {
            toUpdateProduct.setStockStatus(ProductStockStatus.SOLD_OUT);
        }

        return this.adminRepository.save(toUpdateProduct);
    }
    public void RemoveProduct(Long productId) {
        ProductEntity toRemoveProduct = adminRepository.findById(productId).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND, "product-list"));

        this.adminRepository.delete(toRemoveProduct);
    }
}
