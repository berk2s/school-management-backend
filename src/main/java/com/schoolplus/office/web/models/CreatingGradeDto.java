package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatingGradeDto {

    @NotNull
    private GradeLevel gradeLevel;

    @Size(max = 300)
    private String gradeTag;

    @NotNull
    private String advisorTeacher;

    private List<String> students = new ArrayList<>();

}
