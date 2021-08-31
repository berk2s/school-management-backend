package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditingAppointmentDto {

    @Size(max = 99)
    private String appointmentName;

    @Size(max = 300)
    private String appointmentNote;

    private LocalDateTime appointmentStartDate;

    private LocalDateTime appointmentEndDate;

    private String studentId;

    private String teacherId;

}
