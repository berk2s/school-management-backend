package com.schoolplus.office.web.models;

import lombok.Getter;

@Getter
public enum FieldType {
    _NONE(-1),
    NUMERIC(0),
    STRING(1),
    FORMULA(2),
    BLANK(3),
    BOOLEAN(4),
    ERROR(5);

    private final Integer code;

    private FieldType(Integer code) {
        this.code = code;
    }

}
