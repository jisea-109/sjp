package com.tinystop.sjp.Product;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tinystop.sjp.Cart.AddToCartDto;
import com.tinystop.sjp.Exception.CustomException;
import com.tinystop.sjp.Review.ReviewEntity;
import com.tinystop.sjp.Review.ReviewService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/")
@Controller
public class ProductController {

    private final ProductService productService;
    private final ReviewService reviewService;

    @GetMapping("find-product")
    public String GetProducts(@RequestParam(name = "search", required = false, defaultValue = "") String name, Model model) {
        List<ProductEntity> products;

        if (name == null || name.isEmpty()) {
            products = productService.GetProducts(""); // 기본 값 설정
        } else {
            products = productService.GetProducts(name);
        }
        Map<Long, BigDecimal> productRatings = new HashMap<>();
        for (ProductEntity product : products) {
            try {
                BigDecimal rating = reviewService.getReviewAverage(product.getId());
                productRatings.put(product.getId(), rating);
            } catch (CustomException error) {
                productRatings.put(product.getId(), BigDecimal.ZERO);
            }
        }
        model.addAttribute("products", products);
        model.addAttribute("productRatings", productRatings);
        model.addAttribute("addToCart", new AddToCartDto());
        return "product-list";
    }
    
    @GetMapping("product/detail")
    public String GetProductDetail(@RequestParam("id") Long productId, Model model) {
        ProductEntity product = productService.GetProduct(productId);
        List<ReviewEntity> reviewList = reviewService.productReviewList(productId);

        model.addAttribute("product", product);
        model.addAttribute("addToCart", new AddToCartDto());
        model.addAttribute("reviewList", reviewList);
        
        return "product-detail";
    }
    
}
