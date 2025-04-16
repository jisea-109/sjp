package com.tinystop.sjp.Type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    ALREADY_EXIST_USER("ALREADY EXIST USER."),
    DUPLICATE_EMAIL_FOUND("DUPLICATE EMAIL FOUND"),
    ID_NOT_FOUND("ID NOT FOUND."),
    INCORRECT_PASSWORD("INCORRECT PASSWORD"),
    PASSWORD_DOES_NOT_MATCH("PASSWORD DOES NOT MATCH"),
    
    ALREADY_EXIST_PRODUCT("ALREADY EXIST PRODUCT."),
    PRODUCT_NOT_FOUND("PRODUCT NOT FOUND"),

    USER_NOT_FOUND("USER NOT FOUND"),

    INAPPROPRIATE_QUANTITY_VALUE("INAPPROPRIATE QUANTITY VALUE"),
    CART_NOT_FOUND("CART NOT FOUND"),

    OUT_OF_STOCK("OUT OF STOCK"),
    NOT_ENOUGH_STOCK("NOT ENOUGH STOCK"),
    ORDER_NOT_FOUND("ORDER NOT FOUND"),

    FAILED_TO_SEND_EMAIL("FAILED TO SEND EMAIL"),
    FAILED_TO_READ_TEMPLATE("FAILED TO READ TEMPLATE"),
    INCORRECT_SECURITY_CODE("INCORRECT SECURITY CODE"),
    EMAIL_NOT_VERIFIED("EMAIL NOT VERIFIED"),
    SECURITY_CODE_EXPIRED("SECURITY CODE EXPIRED"),

    REVIEW_NOT_FOUND("REVIEW NOT FOUND"),
    NO_PERMISSION_TO_EDIT("NO PERMISSION TO EDIT"),
    FAILED_TO_UPLOAD_IMAGE("FAILED TO UPLOAD IMAGE"),
    TOO_MANY_IMAGES_TO_UPLOAD("TOO MANY IMAGES TO UPLOAD"),
    UNSUPPORTED_FILE_TYPE("UNSUPPORTED FILE TYPE"),
    PATH_TRAVERSAL_DETECTED("PATH TRAVERSAL DETECTED"),
    ALREADY_EXIST_REVIEW("ALREADY EXIST REVIEW");

    private final String message;

}
