package com.schoolplus.office.web.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContinuityDto {

    private String continuityId;

    private SyllabusDto syllabus;

    private ClassroomDto classroom;

    private StudentDto student;

    private OrganizationDto organization;

    private Boolean isAbsent;

    private Timestamp createdAt;

    private Timestamp lastModifiedAt;

}
