package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HomeworkDto {

    private Long homeworkId;

    private String homeworkDescription;

    private ClassroomDto classroom;

    private TeacherDto teacher;

    private SyllabusDto syllabus;

    private LocalDateTime dueDate;

    private Timestamp createdAt;

    private Timestamp lastModifiedAt;

}
