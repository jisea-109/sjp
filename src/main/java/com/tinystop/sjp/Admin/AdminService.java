package com.tinystop.sjp.Admin;

import static com.tinystop.sjp.Type.ErrorCode.ALREADY_EXIST_PRODUCT;
import static com.tinystop.sjp.Type.ErrorCode.PRODUCT_NOT_FOUND;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import com.tinystop.sjp.Exception.CustomException;
import com.tinystop.sjp.Product.ProductEntity;
import com.tinystop.sjp.Type.ProductStockStatus;

@RequiredArgsConstructor
@Transactional
@Service
public class AdminService {
    
    private final AdminRepository adminRepository;

    public ProductEntity AddProduct(AdminManageProductDto product) {
        boolean exists = adminRepository.existsByName(product.getName());
        
        if (exists) { 
            throw new CustomException(ALREADY_EXIST_PRODUCT,"admin");
        }
        
        return adminRepository.save(product.toEntity());
    }
    public ProductEntity GetProductById(Long id) {
        return adminRepository.findById(id).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND,"product-list"));
    }
    public ProductEntity UpdateProduct(AdminModifyProductDto product) {
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

        return adminRepository.save(toUpdateProduct);
    }
    public void RemoveProduct(Long productId) {
        ProductEntity toRemoveProduct = adminRepository.findById(productId).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND, "product-list"));

        this.adminRepository.delete(toRemoveProduct);
    }
}
