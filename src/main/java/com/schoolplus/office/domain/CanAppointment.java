package com.schoolplus.office.domain;

import java.util.List;
import java.util.UUID;

public interface CanAppointment {
    UUID getId();
    List<Appointment> getAppointments();
}
