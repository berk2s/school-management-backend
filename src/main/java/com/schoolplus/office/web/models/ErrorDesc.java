package com.schoolplus.office.web.models;

import lombok.Getter;

@Getter
public enum ErrorDesc {

    USERNAME_NOT_FOUND("User not found by given username"),
    BAD_CREDENTIALS("Bad credentials"),
    INVALID_TOKEN("Invalid token"),
    USER_HAS_NOT_SCOPE("Invalid scope requested for user"),
    SERVER_ERROR("Server error");

    private final String desc;

    ErrorDesc(String desc) {
        this.desc = desc;
    }
}
