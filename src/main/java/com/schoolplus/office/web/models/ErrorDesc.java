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
    SYLLABUS_NOT_FOUND("Syllabus not found", 22),
    CONTINUITY_NOT_FOUND("Continuity not found", 23),
    INVALID_GRANT_TYPE("Invalid grant type", 24),
    HOMEWORK_NOT_FOUND("Homework not found", 25),
    PERSONAL_HOMEWORK_NOT_FOUND("Personal Homework not found", 26),
    EXAM_TYPE_NOT_FOUND("Exam Type not found", 27),
    EXAM_SKELETON_NOT_FOUND("Exam Skeleton not found", 28),
    EXAM_NOT_FOUND("Exam not found", 29),
    EXAM_FIELD_NOT_FOUND("Exam Field not found", 30),
    FILE_NOT_READABLE("File not readable", 31),
    STUDENT_CELL_NOT_DEFINED("Student cell not defined", 32),
    CLASSROOM_CELL_NOT_DEFINED("Classroom cell not defined", 33),
    GRADE_CELL_NOT_DEFINED("Grade cell not defined", 34),
    EXAM_CELL_NOT_DEFINED("Exam cell not defined", 35),
    SORTABLE_CELL_NOT_DEFINED("Sortable cell not defined", 36),
    EXAM_RESULT_NOT_FOUND("Exam result not found", 37),
    EXAM_RESULT_ITEM_NOT_FOUND("Exam result item not found", 38),
    SUPPORT_REQUEST_NOT_FOUND("Support request not found", 39),
    SUPPORT_THREAD_NOT_FOUND("Support thread not found", 40),
    FAILED_IMAGE_UPLOAD("Failed to upload image", 41),
    INVALID_CONTENT_TYPE("Invalid content type", 42);

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
