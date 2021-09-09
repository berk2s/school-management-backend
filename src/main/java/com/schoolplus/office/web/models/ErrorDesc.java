package com.schoolplus.office.web.models;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum ErrorDesc {

    USERNAME_NOT_FOUND("User not found by given username", 1),
    BAD_CREDENTIALS("Bad credentials",2),
    INVALID_TOKEN("Invalid token", 3),
    USER_HAS_NOT_SCOPE("Invalid scope requested for user", 4),
    BAD_REQUEST("Bad request", 5),
    USER_NOT_FOUND("User not found", 6),
    AUTHORITY_NOT_FOUND("Authority not found", 7),
    ROLE_NOT_FOUND("Role not found", 8),
    PARENT_NOT_FOUND("Parent not found", 9),
    STUDENT_NOT_FOUND("Student not found", 10),
    TEACHING_SUBJECT_NOT_FOUND("Teaching subject not found", 11),
    TEACHER_NOT_FOUND("Teacher not found", 12),
    CLASSROOM_NOT_FOUND("Classroom not found", 13),
    APPOINTMENT_NOT_FOUND("Appointment not found", 14),
    TEACHER_NOT_AVAILABLE_FOR_APPOINTMENT("The Teacher is not available for the taken appointment", 15),
    STUDENT_NOT_AVAILABLE_FOR_APPOINTMENT("The Student is not available for the taken appointment", 16),
    SERVER_ERROR("Server error", 17),
    ANNOUNCEMENT_NOT_FOUND("Announcement not found", 18),
    ORGANIZATION_NOT_FOUND("Organization not found", 19),
    GRADE_NOT_FOUND("Grade not found", 20),
    LESSON_NOT_FOUND("Lesson not found", 21),
    SYLLABUS_NOT_FOUND("Syllabus not found", 22);

    private final String desc;
    private final Integer code;
    private static final Map<String, Integer> errorMap =  new HashMap<>();

    ErrorDesc(String desc, Integer code) {
        this.desc = desc;
        this.code = code;
    }

    static {
        for (ErrorDesc errorDesc : ErrorDesc.values()) {
            errorMap.put(errorDesc.getDesc(), errorDesc.getCode());
        }
    }

    static public Integer getCodeFormDesc(String desc) {
        return errorMap.get(desc);
    }
}
