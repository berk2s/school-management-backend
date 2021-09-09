package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditingContinuityDto {

    private Long syllabusId;

    private Long classroomId;

    private String studentId;

    private Long organizationId;

    private Boolean isAbsent;

}
