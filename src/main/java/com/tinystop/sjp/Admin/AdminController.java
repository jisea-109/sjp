package com.tinystop.sjp.Admin;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tinystop.sjp.Admin.ManageProductDto.ManageProduct;
import com.tinystop.sjp.Product.ProductEntity;
import com.tinystop.sjp.type.ProductCategory;

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
        return "admin";
    }

    @PostMapping("/admin/add-product")
    public String AddProducts(@ModelAttribute ManageProduct product) {
        adminservice.AddProduct(product);
        return "redirect:/admin";
    }

    @PostMapping("/admin/remove-product")
        public String RemoveProduct(@ModelAttribute ManageProduct product) {
            adminservice.RemoveProduct(product);
            return "redirect:/find-product";
        }
        
    @GetMapping("/admin/update-product")
    public String UpdateProductPage(@RequestParam("id") Long id, Model model) {
        ProductEntity product = adminservice.GetProductById(id);
        model.addAttribute("product", product);

        model.addAttribute("productCategories", Arrays.asList(ProductCategory.values()));
        return "update-product-detail";
    }

    @PostMapping("/admin/update-product-detail")
    public String UpdateProductDetailPage(@ModelAttribute ManageProduct product) {
        adminservice.UpdateProduct(product);
        return "redirect:/find-product";
    }
    
}
