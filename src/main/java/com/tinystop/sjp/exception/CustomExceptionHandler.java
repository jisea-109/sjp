package com.tinystop.sjp.Exception;

import java.util.List;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.tinystop.sjp.Auth.AuthDto.SignUpDto;
import com.tinystop.sjp.Product.Category.ProductCategoryEntity;
import com.tinystop.sjp.Product.Category.ProductCategoryService;

import lombok.RequiredArgsConstructor;

@ControllerAdvice
@RequiredArgsConstructor
public class CustomExceptionHandler {

    private final ProductCategoryService productCategoryService;

    @ExceptionHandler(CustomException.class)
    public ModelAndView handleCustomException(CustomException ex) {
        String viewName = ex.getTargetView();

        if (viewName == null || viewName.isEmpty()) {
            viewName = "error"; // 기본 에러 페이지
        }

        ModelAndView mav = new ModelAndView(viewName);
        mav.addObject("errorMessage", ex.getErrorMessage()); // 메시지 전달

        if ("signup".equals(viewName)) {
            mav.addObject("signup", new SignUpDto()); // signup 누락 방지
        }

        if ("main".equals(viewName) || "admin".equals(viewName) || "update-product-detail".equals(viewName)) {
            List<ProductCategoryEntity> productCategories = productCategoryService.getProductCategoryList();
            mav.addObject("productCategories", productCategories); // product category 누락 방지
        }
        return mav;
    }
}
