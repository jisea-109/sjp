package com.tinystop.sjp.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    ALREADY_EXIST_USER("ALREADY EXIST USER."),
    ID_NOT_FOUND("ID NOT FOUND."),
    INCORRECT_PASSWORD("INCORRECT PASSWORD"),
    
    ALREADY_EXIST_PRODUCT("ALREADY EXIST PRODUCT."),
    PRODUCT_NOT_FOUND("PRODUCT NOT FOUND");
    private final String message;

}
