package com.schoolplus.office.web.models;

import lombok.Getter;

@Getter
public enum GrantType {
    REFRESH_TOKEN("refresh_token"),
    CHECK_TOKEN("check_token"),
    REVOKE("revoke");

    private final String grant;

    private GrantType(String grant) {
        this.grant = grant;
    }
}
