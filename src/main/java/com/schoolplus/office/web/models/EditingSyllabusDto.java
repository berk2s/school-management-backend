package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditingSyllabusDto {

    @Size(max = 499)
    private String syllabusNote;

    private Long classroomId;

    private Long lessonId;

    private String teacherId;

    private Long organizationId;

    private LocalDateTime syllabusStartDate;

    private LocalDateTime syllabusEndDate;

}
