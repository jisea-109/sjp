package com.tinystop.sjp.Exception;

import com.tinystop.sjp.Type.ErrorCode;

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
