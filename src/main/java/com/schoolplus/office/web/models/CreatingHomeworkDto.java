package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatingHomeworkDto {

    @NotNull
    private Long classroomId;

    @NotNull
    private String teacherId;

    @NotNull
    private Long syllabusId;

    @Size(min = 10, max = 99999)
    private String homeworkDescription;

    @NotNull
    private LocalDateTime dueDate;

}
