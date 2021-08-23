package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditTeacherDto extends EditUserDto {

    private List<Long> addedTeachingSubjects = new ArrayList<>();

    private List<Long> removeTeachingSubjects = new ArrayList<>();

}
