package com.tinystop.sjp.Product;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import lombok.RequiredArgsConstructor;

import com.tinystop.sjp.Cart.AddToCartDto;
import com.tinystop.sjp.Exception.CustomException;
import com.tinystop.sjp.Review.ReviewEntity;
import com.tinystop.sjp.Review.ReviewService;
import com.tinystop.sjp.Type.ProductCategory;

@RequiredArgsConstructor
@RequestMapping("/")
@Controller
public class ProductController {

    private final ProductService productService;
    private final ReviewService reviewService;

    @GetMapping("")
    public String home(Model model) {
        List<ProductCategory> productCategories = Arrays.asList(ProductCategory.values());
        model.addAttribute("productCategories", productCategories);
        return "main";
    }

    @GetMapping("find-product")
    public String GetProducts(@RequestParam(name = "search", required = false, defaultValue = "") String name, 
                                            @PageableDefault(size = 10) Pageable pageable,
                                            Model model) {
        Page<LoadProductDto> products = productService.getProductsByName(name, pageable);

        Map<Long, BigDecimal> productRatings = new HashMap<>();
        for (LoadProductDto product : products) {
            try {
                BigDecimal rating = reviewService.getReviewAverage(product.getId());
                productRatings.put(product.getId(), rating);
            } catch (CustomException error) {
                productRatings.put(product.getId(), BigDecimal.ZERO);
            }
        }
        
        model.addAttribute("products", products);
        model.addAttribute("searchValue", name);
        model.addAttribute("productRatings", productRatings);
        model.addAttribute("addToCart", new AddToCartDto());
        model.addAttribute("currentUrl", "/find-product");
        return "product-list";
    }
    
    @GetMapping("find-product/date")
    public String GetProductsOrderByDate(@RequestParam("searchValue") String searchValue,
                                         @PageableDefault(size = 10) Pageable pageable,
                                         Model model) {
        Page<LoadProductDto> products;

        boolean isCategory = Arrays.stream(ProductCategory.values()).anyMatch(category -> category.name().equalsIgnoreCase(searchValue));
    
        if (isCategory) {
            products = productService.getProductsByComponentOrderByDate(searchValue, pageable);
        } else {
            products = productService.getProductsByNameOrderByDate(searchValue, pageable);
        }

        Map<Long, BigDecimal> productRatings = new HashMap<>();
        for (LoadProductDto product : products) {
            try {
                BigDecimal rating = reviewService.getReviewAverage(product.getId());
                productRatings.put(product.getId(), rating);
            } catch (CustomException error) {
                productRatings.put(product.getId(), BigDecimal.ZERO);
            }
        }

        model.addAttribute("products", products);
        model.addAttribute("searchValue", searchValue);
        model.addAttribute("productRatings", productRatings); 
        model.addAttribute("addToCart", new AddToCartDto());
        model.addAttribute("currentUrl", "/find-product/date");
        
        return "product-list";
    }
    
    @GetMapping("find-product/sales")
    public String GetProductsOrderBySales(@RequestParam("searchValue") String searchValue,
                                          @PageableDefault(size = 10) Pageable pageable,
                                          Model model) {

        Page<LoadProductDto> products;
        boolean isCategory = Arrays.stream(ProductCategory.values()).anyMatch(category -> category.name().equalsIgnoreCase(searchValue));
    
        if (isCategory) {
            products = productService.getProductsByComponentOrderBySales(searchValue, pageable);
        } else {
            products = productService.getProductsByNameOrderByDate(searchValue, pageable);
        }

        Map<Long, BigDecimal> productRatings = new HashMap<>();
        for (LoadProductDto product : products) {
            try {
                BigDecimal rating = reviewService.getReviewAverage(product.getId());
                productRatings.put(product.getId(), rating);
            } catch (CustomException error) {
                productRatings.put(product.getId(), BigDecimal.ZERO);
            }
        }
        
        model.addAttribute("products", products);
        model.addAttribute("searchValue", searchValue);
        model.addAttribute("productRatings", productRatings); 
        model.addAttribute("addToCart", new AddToCartDto());
        model.addAttribute("currentUrl", "/find-product/sales");
        
        return "product-list";
    }
    
    @GetMapping("find-product/component")
    public String GetProductsByComponent(@RequestParam("category") String component, 
                                         @PageableDefault(size = 10) Pageable pageable,
                                         Model model) {
        Page<LoadProductDto> products = productService.getProductsByComponent(component, pageable);

        Map<Long, BigDecimal> productRatings = new HashMap<>();
        for (LoadProductDto product : products) {
            try {
                BigDecimal rating = reviewService.getReviewAverage(product.getId());
                productRatings.put(product.getId(), rating);
            } catch (CustomException error) {
                productRatings.put(product.getId(), BigDecimal.ZERO);
            }
        }

        model.addAttribute("products", products);
        model.addAttribute("searchValue", component);
        model.addAttribute("productRatings", productRatings);
        model.addAttribute("addToCart", new AddToCartDto());
        model.addAttribute("currentUrl", "/find-product/component");
        return "product-list";
    }

    @GetMapping("find-product/reviews")
    public String GetProductsOrderByReviews(@RequestParam("searchValue") String searchValue,
                                            @PageableDefault(size = 10) Pageable pageable,
                                            Model model) {
        Page<LoadProductDto> products;

        boolean isCategory = Arrays.stream(ProductCategory.values()).anyMatch(category -> category.name().equalsIgnoreCase(searchValue));
    
        if (isCategory) {
            products = productService.getProductsByComponentOrderByReviews(searchValue, pageable);
        } else {
            products = productService.getProductsByNameOrderByReviews(searchValue, pageable);
        }

        Map<Long, BigDecimal> productRatings = new HashMap<>();
        for (LoadProductDto product : products) {
            try {
                BigDecimal rating = reviewService.getReviewAverage(product.getId());
                productRatings.put(product.getId(), rating);
            } catch (CustomException error) {
                productRatings.put(product.getId(), BigDecimal.ZERO);
            }
        }

        model.addAttribute("products", products);
        model.addAttribute("searchValue", searchValue);
        model.addAttribute("productRatings", productRatings);
        model.addAttribute("addToCart", new AddToCartDto());
        model.addAttribute("currentUrl", "/find-product/reviews");

        return "product-list";
    }

    @GetMapping("product/detail")
    public String GetProductDetail(@RequestParam("id") Long productId, Model model) {
        ProductEntity product = productService.getProduct(productId);
        List<ReviewEntity> reviewList = reviewService.productReviewList(productId);

        model.addAttribute("product", product);
        model.addAttribute("addToCart", new AddToCartDto());
        model.addAttribute("reviewList", reviewList);
        
        return "product-detail";
    }
}
