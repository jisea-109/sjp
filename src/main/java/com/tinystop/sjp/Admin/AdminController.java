package com.tinystop.sjp.Admin;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tinystop.sjp.Exception.CustomException;
import com.tinystop.sjp.Product.ProductEntity;
import com.tinystop.sjp.Type.ProductCategory;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminservice;

    @GetMapping("/admin")
     public String AdminPage(Model model) {
        List<ProductCategory> productCategories = Arrays.asList(ProductCategory.values());
        model.addAttribute("productCategories", productCategories);
        model.addAttribute("addProduct", new AdminAddProductDto());
        return "admin";
    }

    @PostMapping("/admin/add-product")
    public String AddProducts(@ModelAttribute("addProduct") @Valid AdminAddProductDto product, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("addProduct", product);
            return "admin";
        }
        try {
            adminservice.addProduct(product);
        } catch(CustomException error) {
            model.addAttribute("addProduct", product);
            model.addAttribute("errorMessage", error.getMessage());
            return "admin";
        }
        return "redirect:/admin";
    }

    @PostMapping("/admin/remove-product")
    public String RemoveProduct(@RequestParam("id") Long productId) {
        adminservice.removeProduct(productId);
        return "redirect:/find-product";
    }
        
    @GetMapping("/admin/update-product")
    public String UpdateProductPage(@RequestParam("id") Long id, Model model) {
        ProductEntity product = adminservice.getProductById(id);
        AdminModifyProductDto modifyProductDto = AdminModifyProductDto.from(product);
        
        model.addAttribute("modifyProduct", modifyProductDto);
        model.addAttribute("productCategories", Arrays.asList(ProductCategory.values()));
        return "update-product-detail";
    }

    @PostMapping("/admin/update-product-detail")
    public String UpdateProductDetailPage(@ModelAttribute("modifyProduct") @Valid AdminModifyProductDto modifyProduct, BindingResult bindingResult,  Model model) {
        if (bindingResult.hasErrors()) {
            for (FieldError error : bindingResult.getFieldErrors()) {
                System.out.println("Field: " + error.getField() + " / Message: " + error.getDefaultMessage());
            }
            model.addAttribute("update-product-detail", modifyProduct);
            return "update-product-detail";
        }
        try {
            adminservice.updateProduct(modifyProduct);
        } catch(CustomException error) {
            model.addAttribute("update-product-detail", modifyProduct);
            model.addAttribute("errorMessage", error.getMessage());
            return "update-product-detail";
        }
        adminservice.updateProduct(modifyProduct);
        return "redirect:/find-product";
    }
    
}
