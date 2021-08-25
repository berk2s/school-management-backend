package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatingGradeDto {

    @NotNull
    private GradeLevel gradeLevel;

    private String gradeTag;

    @NotNull
    private String advisorTeacher;

    private List<String> students = new ArrayList<>();

}
