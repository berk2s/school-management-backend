package com.schoolplus.office.web.models;

import lombok.*;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatingStudentDto extends CreatingUserDto {

    @NotNull
    private GradeType gradeType;

    @NotNull
    private GradeLevel gradeLevel;

}
