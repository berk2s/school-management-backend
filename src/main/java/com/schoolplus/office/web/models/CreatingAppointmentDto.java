package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatingAppointmentDto {

    @Size(max = 99)
    private String appointmentName;

    @Size(max = 300)
    private String appointmentNote;

    @NotNull
    private LocalDateTime appointmentStartDate;

    @NotNull
    private LocalDateTime appointmentEndDate;

    @NotNull
    private String studentId;

    @NotNull
    private String teacherId;

}
