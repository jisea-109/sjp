package com.tinystop.sjp.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ModelAndView handleCustomException(CustomException ex) {
        String viewName = ex.getTargetView(); // 사용자가 지정한 타겟 뷰

        // targetView가 null일 경우 fallback 페이지 설정
        if (viewName == null || viewName.isEmpty()) {
            viewName = "error"; // 기본 에러 페이지
        }

        ModelAndView mav = new ModelAndView(viewName);
        mav.addObject("errorMessage", ex.getErrorMessage()); // 메시지 전달
        return mav;
    }
}
