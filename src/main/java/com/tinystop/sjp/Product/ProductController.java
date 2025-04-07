package com.tinystop.sjp.Product;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tinystop.sjp.Cart.AddToCartDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/")
@Controller
public class ProductController {
    private final ProductService productService;

    @GetMapping("find-product")
    public String GetProducts(@RequestParam(name = "search", required = false, defaultValue = "") String name, Model model) {
        List<ProductEntity> products;

        if (name == null || name.isEmpty()) {
            products = productService.GetProducts(""); // 기본 값 설정
        } else {
            products = productService.GetProducts(name);
        }

        model.addAttribute("products", products);
        model.addAttribute("addToCart", new AddToCartDto());
        return "product-list";
    }
}
