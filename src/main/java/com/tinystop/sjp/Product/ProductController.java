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
import com.tinystop.sjp.Review.ReviewEntity;
import com.tinystop.sjp.Review.ReviewService;
import com.tinystop.sjp.Type.ProductCategory;

@RequiredArgsConstructor
@RequestMapping("/")
@Controller
public class ProductController {

    private final ProductService productService;
    private final ReviewService reviewService;
    private final ProductRatingService productRatingService;

    /**
     * 메인 홈페이지
     * @param model 클라이언트에 전달할 데이터를 담은 객체, 여기서는 product category 리스트들 보내기 위해서 사용함
     * @return 메인 페이지 main.html
     */
    @GetMapping("")
    public String home(Model model) {
        List<ProductCategory> productCategories = Arrays.asList(ProductCategory.values());
        model.addAttribute("productCategories", productCategories);
        return "main";
    }

    /**
     * Service.getProductsByName 함수를 통해서 Product 검색, 제일 많이 쓰이는 함수.
     * @param name 검색한 keyword 값
     * @param pageable 페이지 번호, 크기, 정보 담은 객체
     * @param model 클라이언트에 전달할 데이터를 담은 객체
     * @return 필터링된 product들 표시하는 html
     */
    @GetMapping("find-product")
    public String GetProducts(@RequestParam(name = "search", required = false, defaultValue = "") String searchValue, 
                                            @PageableDefault(size = 10) Pageable pageable,
                                            Model model) {

        Page<LoadProductDto> products = productService.getProductsByName(searchValue, pageable); // 필터링된 product 리스트

        Map<Long, BigDecimal> productRatings = new HashMap<>(); // 평균 리뷰 점수 구하기 위해 선언
        productRatings = productRatingService.getAverageRating(productRatings, products);
        
        model.addAttribute("products", products); // product 목록 전달
        model.addAttribute("searchValue", searchValue); // 검색값 전달
        model.addAttribute("productRatings", productRatings); // product 목록 각 리뷰 평균 전달
        model.addAttribute("addToCart", new AddToCartDto()); // cart에 추가하기 위한 AddToCartdto 전달
        model.addAttribute("currentUrl", "/find-product"); // 페이지 이동 때 기준 URL 전달
        return "product-list";
    }

    /**
     * 검색 또는 선택한 Component에 포함되는 product들을 최신 등록 순으로 나열하는 함수. 
     * searchValue가 ProductCategory에 있는 Component 변수들 중 이름이 같을 경우 그 Component에 포함되는 product들 나열.
     * @param searchValue 검색값
     * @param pageable 페이지 번호, 크기, 정보 담은 객체
     * @param model 클라이언트에 전달할 데이터를 담은 객체
     * @return 필터링된 product들 표시하는 html
     */
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

        Map<Long, BigDecimal> productRatings = new HashMap<>(); // 평균 리뷰 점수 구하기 위해 선언
        productRatings = productRatingService.getAverageRating(productRatings, products);

        model.addAttribute("products", products); // product 목록 전달
        model.addAttribute("searchValue", searchValue); // 검색값 전달
        model.addAttribute("productRatings", productRatings); // product 목록 각 리뷰 평균 전달
        model.addAttribute("addToCart", new AddToCartDto()); // cart에 추가하기 위한 AddToCartdto 전달
        model.addAttribute("currentUrl", "/find-product/date"); // 페이지 이동 때 기준 URL 전달
        
        return "product-list";
    }
    
    /** 검색 또는 선택한 Component에 포함되는 product들을 판매량에 따라서 제일 판매량이 높은 순서대로 나열하는 함수.
     * searchValue가 ProductCategory에 있는 Component 변수들 중 이름이 같을 경우 그 Component에 포함되는 product들 나열.
     * @param searchValue 검색값
     * @param pageable 페이지 번호, 크기, 정보 담은 객체
     * @param model 클라이언트에 전달할 데이터를 담은 객체
     * @return 필터링된 product들 표시하는 html
     */
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

        Map<Long, BigDecimal> productRatings = new HashMap<>(); // 평균 리뷰 점수 구하기 위해 선언
        productRatings = productRatingService.getAverageRating(productRatings, products);
        
        model.addAttribute("products", products); // product 목록 전달
        model.addAttribute("searchValue", searchValue); // 검색값 전달
        model.addAttribute("productRatings", productRatings); // product 목록 각 리뷰 평균 전달
        model.addAttribute("addToCart", new AddToCartDto()); // cart에 추가하기 위한 AddToCartdto 전달
        model.addAttribute("currentUrl", "/find-product/sales"); // 페이지 이동 때 기준 URL 전달
        
        return "product-list";
    }
    
    /**
     * main.html에서 나열되있는 Component들 중 고른 Component에 해당하는 product들을 나열하는 함수
     * @param component 선택한 Component
     * @param pageable 페이지 번호, 크기, 정보 담은 객체
     * @param model 클라이언트에 전달할 데이터를 담은 객체
     * @return 필터링된 product들 표시하는 html
     */
    @GetMapping("find-product/component")
    public String GetProductsByComponent(@RequestParam("category") String component, 
                                         @PageableDefault(size = 10) Pageable pageable,
                                         Model model) {

        Page<LoadProductDto> products = productService.getProductsByComponent(component, pageable);

        Map<Long, BigDecimal> productRatings = new HashMap<>(); // 평균 리뷰 점수 구하기 위해 선언
        productRatings = productRatingService.getAverageRating(productRatings, products);

        model.addAttribute("products", products); // product 목록 전달
        model.addAttribute("searchValue", component); // component 값 전달
        model.addAttribute("productRatings", productRatings); // product 목록 각 리뷰 평균 전달
        model.addAttribute("addToCart", new AddToCartDto()); // cart에 추가하기 위한 AddToCartdto 전달
        model.addAttribute("currentUrl", "/find-product/component"); // 페이지 이동 때 기준 URL 전달
        return "product-list";
    }

    /** 검색 또는 선택한 Component에 포함되는 product들을 리뷰랑에 따라서 제일 리뷰량이 높은대로 나열하는 함수.
     * searchValue가 ProductCategory에 있는 Component 변수들 중 이름이 같을 경우 그 Component에 포함되는 product들 나열.
     * @param searchValue 검색값
     * @param pageable 페이지 번호, 크기, 정보 담은 객체
     * @param model 클라이언트에 전달할 데이터를 담은 객체
     * @return 필터링된 product들 표시하는 html
     */
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

        Map<Long, BigDecimal> productRatings = new HashMap<>(); // 평균 리뷰 점수 구하기 위해 선언
        productRatings = productRatingService.getAverageRating(productRatings, products);

        model.addAttribute("products", products); // product 목록 전달
        model.addAttribute("searchValue", searchValue); // 검색값 전달
        model.addAttribute("productRatings", productRatings); // product 목록 각 리뷰 평균 전달
        model.addAttribute("addToCart", new AddToCartDto()); // cart에 추가하기 위한 AddToCartdto 전달
        model.addAttribute("currentUrl", "/find-product/reviews"); // 페이지 이동 때 기준 URL 전달

        return "product-list";
    }

    /**
     * product-list.html에 나열되어있는 product를 고르게 되면 그 product의 정보들을 가져와주는 함수
     * @param productId 선택한 product ID
     * @param model 클라이언트에 전달할 데이터를 담은 객체
     * @return 선택한 product의 정보를 표시하는 html
     */
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
