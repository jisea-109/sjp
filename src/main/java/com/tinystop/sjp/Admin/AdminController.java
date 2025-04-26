package com.tinystop.sjp.Admin;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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
    public String AddProducts(@ModelAttribute("addProduct") @Valid AdminAddProductDto addProductRequest, 
                              BindingResult bindingResult,
                              @RequestParam("uploadImages") MultipartFile[] uploadImages,
                              Model model) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());

            model.addAttribute("errorMessage", errors);
            model.addAttribute("addProduct", addProductRequest);
            model.addAttribute("productCategories", Arrays.asList(ProductCategory.values()));
            return "admin";
        }
        try {
            adminservice.addProduct(addProductRequest, uploadImages);
        } catch(CustomException error) {
            model.addAttribute("addProduct", addProductRequest);
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
        AdminEditProductDto editProductDto = AdminEditProductDto.from(product);
        
        model.addAttribute("editProduct", editProductDto);
        model.addAttribute("productCategories", Arrays.asList(ProductCategory.values()));
        return "update-product-detail";
    }

    @PostMapping("/admin/update-product-detail")
    public String UpdateProductDetailPage(@ModelAttribute("editProduct") @Valid AdminEditProductDto editProductRequest,
                                          BindingResult bindingResult,
                                          @RequestParam("uploadImages") MultipartFile[] uploadImages,
                                          Model model) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());

            model.addAttribute("errorMessage", errors);
            model.addAttribute("update-product-detail", editProductRequest);
            model.addAttribute("productCategories", Arrays.asList(ProductCategory.values()));
            return "update-product-detail";
        }
        try {
            adminservice.updateProduct(editProductRequest, uploadImages);
        } catch(CustomException error) {
            model.addAttribute("update-product-detail", editProductRequest);
            model.addAttribute("errorMessage", error.getMessage());
        }
        return "redirect:/find-product";
    }
    
}
