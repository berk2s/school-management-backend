package com.schoolplus.office.web.models;

import lombok.Getter;



@Getter
public enum ErrorDesc {

    USERNAME_NOT_FOUND("User not found by given username"),
    BAD_CREDENTIALS("Bad credentials"),
    INVALID_TOKEN("Invalid token"),
    USER_HAS_NOT_SCOPE("Invalid scope requested for user"),
    BAD_REQUEST("Bad request"),
    USER_NOT_FOUND("User not found"),
    AUTHORITY_NOT_FOUND("Authority not found"),
    ROLE_NOT_FOUND("Role not found"),
    PARENT_NOT_FOUND("Parent not found"),
    STUDENT_NOT_FOUND("Student not found"),
    TEACHING_SUBJECT_NOT_FOUND("Teaching subject not found"),
    TEACHER_NOT_FOUND("Teacher not found"),
    GRADE_NOT_FOUND("Grade not found"),
    SERVER_ERROR("Server error");

    private final String desc;

    ErrorDesc(String desc) {
        this.desc = desc;
    }
}
