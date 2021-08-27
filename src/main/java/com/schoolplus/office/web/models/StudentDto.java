package com.schoolplus.office.web.models;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StudentDto extends UserDto {

    private String gradeType;

    private String gradeLevel;

    private List<ParentDto> parents = new ArrayList<>();

    private GradeDto grade;

}
