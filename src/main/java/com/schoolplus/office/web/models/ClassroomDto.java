package com.schoolplus.office.web.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClassroomDto {

    private Long classRoomId;

    private String classRoomTag;

    private TeacherDto advisorTeacher;

    private List<StudentDto> students = new ArrayList<>();

    private OrganizationDto organization;

    private GradeDto grade;

}
