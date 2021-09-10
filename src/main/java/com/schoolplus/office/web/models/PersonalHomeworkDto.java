package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PersonalHomeworkDto {

    private Long personalHomeworkId;

    private String personalHomeworkName;

    private String personalHomeworkDescription;

    private LocalDateTime dueDate;

    private TeacherDto teacher;

    private StudentDto student;

    private LessonDto lesson;

    private Timestamp createdAt;

    private Timestamp lastModifiedAt;

}
