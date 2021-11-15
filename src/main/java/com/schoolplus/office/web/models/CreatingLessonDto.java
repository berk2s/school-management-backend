package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatingLessonDto {

    @Size(min = 3, max = 100)
    @NotNull
    private String lessonName;

    @NotNull
    private Long organizationId;

}
