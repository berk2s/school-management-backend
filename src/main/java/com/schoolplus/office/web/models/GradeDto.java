package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GradeDto {

    private Long gradeId;

    private String gradeTag;

    private String gradeType;

    private String gradeLevel;

    private TeacherDto advisorTeacher;

    private List<StudentDto> students = new ArrayList<>();

    private OrganizationDto organization;

}
