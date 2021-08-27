package com.schoolplus.office.services;

import com.schoolplus.office.domain.CanAppointment;
import com.schoolplus.office.domain.Student;
import com.schoolplus.office.domain.Teacher;
import com.schoolplus.office.web.models.AppointmentDto;
import com.schoolplus.office.web.models.CreatingAppointmentDto;
import com.schoolplus.office.web.models.EditingAppointmentDto;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentService {

    AppointmentDto getAppointment(UUID appointmentId);

    List<AppointmentDto> getAppointments(Pageable pageable);

    AppointmentDto createAppointment(CreatingAppointmentDto creatingAppointment);

    void updateAppointment(UUID appointmentId, EditingAppointmentDto editingAppointment);

    void deleteAppointment(UUID appointmentId);

    boolean isAppointmentTaken(CanAppointment t, UUID appointmentId, LocalDateTime start, LocalDateTime end);

}
