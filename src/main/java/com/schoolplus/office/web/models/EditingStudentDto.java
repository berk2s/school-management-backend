package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditingStudentDto extends EditingUserDto {

    private GradeType gradeType;

    private GradeLevel gradeLevel;

    @Size(max = 99)
    private List<String> addedParents;

    @Size(max = 99)
    private List<String> deletedParents;

    private Long gradeId;

}
