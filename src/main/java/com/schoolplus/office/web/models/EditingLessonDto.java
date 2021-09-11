package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditingLessonDto {

    @Size(min = 3, max = 20)
    private String lessonName;

    private Long organizationId;

}
