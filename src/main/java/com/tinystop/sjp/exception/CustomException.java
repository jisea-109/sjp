package com.tinystop.sjp.exception;

import com.tinystop.sjp.type.ErrorCode;

import lombok.Getter;

@Getter

public class CustomException extends RuntimeException {
    private ErrorCode errorCode;
    private String errorMessage;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getMessage();
    }
}
