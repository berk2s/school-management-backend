package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditingGradeDto {

    private String gradeTag;

    private String gradeLevel;

    private String advisorTeacher;

    private List<String> addedStudents = new ArrayList<>();

    private List<String> deletedStudents = new ArrayList<>();

}
