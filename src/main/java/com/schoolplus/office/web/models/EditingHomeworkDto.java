package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditingHomeworkDto {

    private Long classroomId;

    private String teacherId;

    private Long syllabusId;

    @Size(min = 10, max = 99999)
    private String homeworkDescription;

    private LocalDateTime dueDate;

}
