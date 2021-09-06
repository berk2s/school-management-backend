package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AppointmentDto {

    private String appointmentId;

    private String appointmentName;

    private String appointmentNote;

    private LocalDateTime appointmentStartDate;

    private LocalDateTime appointmentEndDate;

    private StudentDto student;

    private TeacherDto teacher;

    private OrganizationDto organization;

    private Timestamp createdAt;

    private Timestamp lastModifiedAt;
}
