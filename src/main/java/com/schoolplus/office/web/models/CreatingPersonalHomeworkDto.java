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
public class CreatingPersonalHomeworkDto {

    @Size(max = 99)
    private String personalHomeworkName;

    @Size(max = 99999)
    @NotNull
    private String personalHomeworkDescription;

    @NotNull
    private LocalDateTime dueDate;

    @NotNull
    private String teacherId;

    @NotNull
    private String studentId;

    @NotNull
    private Long lessonId;

}
