package com.schoolplus.office.web.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.schoolplus.office.annotations.Logable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SyllabusDto {

    @Logable(type = LogableType.ID)
    private Long syllabusId;

    private String syllabusNote;

    private ClassroomDto classroom;

    private LessonDto lesson;

    private OrganizationDto organization;

    private TeacherDto teacher;

    private LocalDateTime syllabusStartDate;

    private LocalDateTime syllabusEndDate;

    private Integer lessonDuration;

    private Timestamp createdAt;

    private Timestamp lastModifiedAt;

}
