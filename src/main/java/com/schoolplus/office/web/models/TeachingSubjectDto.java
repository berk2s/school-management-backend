package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TeachingSubjectDto {

    private Long teachingSubjectId;

    private String subjectName;

    private List<TeacherDto> teachers = new ArrayList<>();

    private OrganizationDto organization;

    private Timestamp createdAt;

    private Timestamp lastModifiedAt;

}
