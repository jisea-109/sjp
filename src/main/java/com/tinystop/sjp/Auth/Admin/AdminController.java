package com.tinystop.sjp.Auth.Admin;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tinystop.sjp.Auth.Admin.ManageProductDto;
import com.tinystop.sjp.Exception.CustomException;
import com.tinystop.sjp.Product.ProductEntity;
import com.tinystop.sjp.type.ProductCategory;

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
        model.addAttribute("addProduct", new ManageProductDto());
        return "admin";
    }

    @PostMapping("/admin/add-product")
    public String AddProducts(@ModelAttribute("addProduct") @Valid ManageProductDto product, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("admin", product);
        }
        try {
            adminservice.AddProduct(product);
        } catch(CustomException error) {
            model.addAttribute("admin", product);
            model.addAttribute("errorMessage", error.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/admin/remove-product")
    public String RemoveProduct(@RequestParam("id") Long productId) {
        adminservice.RemoveProduct(productId);
        return "redirect:/find-product";
    }
        
    @GetMapping("/admin/update-product")
    public String UpdateProductPage(@RequestParam("id") Long id, Model model) {
        ProductEntity product = adminservice.GetProductById(id);
        ModifyProductDto modifyProductDto = ModifyProductDto.from(product);
        
        model.addAttribute("modifyProduct", modifyProductDto);
        model.addAttribute("productCategories", Arrays.asList(ProductCategory.values()));
        return "update-product-detail";
    }

    @PostMapping("/admin/update-product-detail")
    public String UpdateProductDetailPage(@ModelAttribute("modifyProduct") @Valid ModifyProductDto modifyProduct, BindingResult bindingResult,  Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("update-product-detail", modifyProduct);
        }
        try {
            adminservice.UpdateProduct(modifyProduct);
        } catch(CustomException error) {
            model.addAttribute("update-product-detail", modifyProduct);
            model.addAttribute("errorMessage", error.getMessage());
        }
        adminservice.UpdateProduct(modifyProduct);
        return "redirect:/find-product";
    }
    
}
