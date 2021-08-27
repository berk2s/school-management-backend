package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditingStudentDto extends EditingUserDto {

    private GradeType gradeType;

    private GradeLevel gradeLevel;

    private List<String> addedParents;

    private List<String> deletedParents;

    private Long gradeId;

}
