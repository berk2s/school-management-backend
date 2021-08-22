package com.schoolplus.office.web.models;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StudentDto extends UserDto {

    private String gradeType;

    private String gradeLevel;

}
