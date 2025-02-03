package com.tinystop.sjp.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    ALREADY_EXIST_USER("ALREADY EXIST USER."),
    ID_NOT_FOUND("ID_NOT_FOUND."),
    INCORRECT_PASSWORD("INCORRECT PASSWORD");
    private final String message;

}
