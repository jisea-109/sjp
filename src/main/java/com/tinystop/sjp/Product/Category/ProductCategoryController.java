package com.tinystop.sjp.Product.Category;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tinystop.sjp.Exception.CustomException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/")
@Controller
public class ProductCategoryController {
    private final ProductCategoryService productCategoryService;

    @GetMapping("manage-product-category")
    public String GetProductCategoryPage(Model model) {
        AddProductCategoryDto addProductCategoryDto = new AddProductCategoryDto();
        List<ProductCategoryEntity> productCategories = productCategoryService.getProductCategoryList();

        model.addAttribute("addProductCategory", addProductCategoryDto);
        model.addAttribute("productCategories", productCategories);
        return "admin-product-category";
    }

    @PostMapping("add-product-category")
    public String AddProductCategory(@ModelAttribute("addProductCategory") @Valid AddProductCategoryDto addProductCategoryRequest,
                                     BindingResult bindingResult,
                                     Model model) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.toList()); // 에러 메세지 리스트
            List<ProductCategoryEntity> productCategories = productCategoryService.getProductCategoryList();
            model.addAttribute("errorMessage", errors);
            model.addAttribute("productCategories", productCategories);
            return "admin-product-category";
        }
        try {
            productCategoryService.addProductCategory(addProductCategoryRequest);
            List<ProductCategoryEntity> productCategories = productCategoryService.getProductCategoryList();
            model.addAttribute("productCategories", productCategories);
        } catch(CustomException error) {
            List<ProductCategoryEntity> productCategories = productCategoryService.getProductCategoryList();
            model.addAttribute("productCategories", productCategories);
            model.addAttribute("errorMessage", error);
            return "redirect:/admin/manage-product-category";
        }
        return "admin-product-category";
    }

    @PostMapping("remove-product-category")
    public String RemoveProductCategory(@RequestParam("productCategoryId") Long productCategoryId, Model model) {
        productCategoryService.removeProductCategory(productCategoryId);
        List<ProductCategoryEntity> productCategories = productCategoryService.getProductCategoryList();
        model.addAttribute("productCategories", productCategories);
        return "redirect:/admin/manage-product-category";
    }
}
