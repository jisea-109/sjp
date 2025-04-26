package com.tinystop.sjp.Exception;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.tinystop.sjp.Auth.AuthDto.SignUpDto;
import com.tinystop.sjp.Type.ProductCategory;

@ControllerAdvice
public class CustomExceptionHandler {

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
        
        List<ProductCategory> productCategories = Arrays.asList(ProductCategory.values());
        if ("main".equals(viewName)) {
            mav.addObject("productCategories", productCategories); // signup 누락 방지
        }
        return mav;
    }
}
