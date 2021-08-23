package com.schoolplus.office.web.models;

import lombok.Getter;

@Getter
public enum GradeType {
    PRIMARY_SCHOOL("İlkokul"),
    MIDDLE_SCHOOL("Ortaokul"),
    HIGH_SCHOOL("Lise"),
    GRADUATED("Mezun");

    private final String type;

    GradeType(String type) {
        this.type = type;
    }

}
