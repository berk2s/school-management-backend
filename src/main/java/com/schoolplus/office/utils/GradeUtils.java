package com.schoolplus.office.utils;

import com.schoolplus.office.web.models.GradeLevel;
import com.schoolplus.office.web.models.GradeType;

import java.util.HashMap;
import java.util.Map;

public final class GradeUtils {

    public final static Map<GradeLevel, GradeType> GRADE_MAP = new HashMap<>() {{
        put(GradeLevel.FIRST_GRADE, GradeType.PRIMARY_SCHOOL);
        put(GradeLevel.SECOND_GRADE, GradeType.PRIMARY_SCHOOL);
        put(GradeLevel.THIRD_GRADE, GradeType.PRIMARY_SCHOOL);
        put(GradeLevel.FOURTH_GRADE, GradeType.PRIMARY_SCHOOL);
        put(GradeLevel.FIFTH_GRADE, GradeType.MIDDLE_SCHOOL);
        put(GradeLevel.SIXTH_GRADE, GradeType.MIDDLE_SCHOOL);
        put(GradeLevel.SEVENTH_GRADE, GradeType.MIDDLE_SCHOOL);
        put(GradeLevel.EIGHTH_GRADE, GradeType.MIDDLE_SCHOOL);
        put(GradeLevel.NINTH_GRADE, GradeType.HIGH_SCHOOL);
        put(GradeLevel.TENTH_GRADE, GradeType.HIGH_SCHOOL);
        put(GradeLevel.ELEVENTH_GRADE, GradeType.HIGH_SCHOOL);
        put(GradeLevel.TWELFTH_GRADE, GradeType.HIGH_SCHOOL);
    }};

    public static GradeType levelConverter(GradeLevel gradeLevel) {
        return GRADE_MAP.get(gradeLevel);
    }

}
