package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatingSyllabusDto {

    @Size(max = 499)
    private String syllabusNote;

    @NotNull
    private Long classroomId;

    @NotNull
    private Long lessonId;

    @NotNull
    private String teacherId;

    @NotNull
    private Long organizationId;

    @NotNull
    private LocalDateTime syllabusStartDate;

    @NotNull
    private LocalDateTime syllabusEndDate;

}
