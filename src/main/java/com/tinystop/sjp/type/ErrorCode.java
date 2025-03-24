package com.tinystop.sjp.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    ALREADY_EXIST_USER("ALREADY EXIST USER."),
    DUPLICATE_EMAIL_FOUND("DUPLICATE EMAIL FOUND"),
    ID_NOT_FOUND("ID NOT FOUND."),
    INCORRECT_PASSWORD("INCORRECT PASSWORD"),
    
    ALREADY_EXIST_PRODUCT("ALREADY EXIST PRODUCT."),
    PRODUCT_NOT_FOUND("PRODUCT NOT FOUND"),

    USER_NOT_FOUND("USER NOT FOUND"),

    CART_NOT_FOUND("CART NOT FOUND"),

    OUT_OF_STOCK("OUT OF STOCK"),
    NOT_ENOUGH_STOCK("NOT ENOUGH STOCK"),
    ORDER_NOT_FOUND("ORDER NOT FOUND");
    private final String message;

}
