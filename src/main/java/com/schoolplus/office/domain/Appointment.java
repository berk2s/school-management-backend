package com.schoolplus.office.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Appointment extends BaseEntity {

    @Column(name = "appointment_name")
    private String appointmentName;

    @Column(name = "appointment_note")
    private String appointmentNote;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, optional = false)
    private Organization organization;

    @Column(name = "appointment_start_date")
    private LocalDateTime appointmentStartDate;

    @Column(name = "appointment_end_date")
    private LocalDateTime appointmentEndDate;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Student student;

}
