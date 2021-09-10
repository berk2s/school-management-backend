package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditingPersonalHomeworkDto {

    @Size(max = 99)
    private String personalHomeworkName;

    @Size(max = 99999)
    private String personalHomeworkDescription;

    private LocalDateTime dueDate;

    private String teacherId;

    private String studentId;

    private Long lessonId;

}
