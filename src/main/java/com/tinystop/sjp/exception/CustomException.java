package com.tinystop.sjp.exception;

import com.tinystop.sjp.type.ErrorCode;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private ErrorCode errorCode;
    private String errorMessage;
    private String targetView;

    public CustomException(ErrorCode errorCode, String targetView) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getMessage();
        this.targetView = targetView;
    }
}
