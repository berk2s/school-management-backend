package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatingContinuityDto {

    @NotNull
    private Long syllabusId;

    @NotNull
    private Long classroomId;

    @NotNull
    private String studentId;

    @NotNull
    private Long organizationId;

    @NotNull
    private Boolean isAbsent;

}
