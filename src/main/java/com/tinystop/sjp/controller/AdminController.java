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
     public String adminPage(Model model) {
        List<ProductCategory> productCategories = Arrays.asList(ProductCategory.values());
        model.addAttribute("productCategories", productCategories);
        return "admin";
    }

    @PostMapping("/admin/addproduct")
    public String addproducts(@ModelAttribute ManageProduct product) {
        adminservice.AddProduct(product);
        return "admin";
    }

    @PostMapping("/admin/modifyproduct")
    public String modifyproducts(@ModelAttribute ManageProduct product) {
        adminservice.ModifyProduct(product);
        return "admin";
    }
    
    @GetMapping("/admin/findproduct")
    public String getproducts(@RequestParam(name = "name", required = false, defaultValue = "") String name, Model model) {
        List<ProductEntity> products;

        if (name == null || name.isEmpty()) {
            products = adminservice.GetProducts(""); // 기본 값 설정
        } else {
            products = adminservice.GetProducts(name);
        }

        model.addAttribute("products", products);

        return "admin-products";
    }
}
