package com.schoolplus.office.web.models;

import lombok.Getter;

@Getter
public enum GradeLevel {
    KINDERGARTEN("Anaokulu"),
    FIRST_GRADE("1. Sınıf"),
    SECOND_GRADE("2. Sınıf"),
    THIRD_GRADE("3. Sınıf"),
    FOURTH_GRADE("4. Sınıf"),
    FIFTH_GRADE("5. Sınıf"),
    SIXTH_GRADE("6. Sınıf"),
    SEVENTH_GRADE("7. Sınıf"),
    EIGHTH_GRADE("8. Sınıf"),
    NINTH_GRADE("9. Sınıf"),
    TENTH_GRADE("10. Sınıf"),
    ELEVENTH_GRADE("11. Sınıf"),
    TWELFTH_GRADE("12. Sınıf");

    private final String gradeYear;

    GradeLevel(String gradeYear) {
        this.gradeYear = gradeYear;
    }
}
