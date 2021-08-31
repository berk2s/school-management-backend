package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditingTeacherDto extends EditingUserDto {

    @Size(max = 99)
    private List<Long> addedTeachingSubjects = new ArrayList<>();

    @Size(max = 99)
    private List<Long> removeTeachingSubjects = new ArrayList<>();

}
