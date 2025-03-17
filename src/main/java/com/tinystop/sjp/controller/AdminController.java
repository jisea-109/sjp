package com.tinystop.sjp.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tinystop.sjp.dto.ManageProductDto.ManageProduct;
import com.tinystop.sjp.entity.ProductEntity;
import com.tinystop.sjp.service.AdminService;
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
        return "admin";
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
