package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.Appointment;
import com.schoolplus.office.web.models.AppointmentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;
import java.util.UUID;

@Mapper(imports = UUID.class, uses = {TeacherMapper.class, StudentMapper.class})
public interface AppointmentMapper {

    @Mappings({
            @Mapping(target = "appointmentId", expression = "java( appointment.getId().toString() )"),
            @Mapping(source = "appointmentName", target = "appointmentName"),
            @Mapping(source = "appointmentNote", target = "appointmentNote"),
            @Mapping(source = "appointmentStartDate", target = "appointmentStartDate"),
            @Mapping(source = "appointmentEndDate", target = "appointmentEndDate"),
            @Mapping(target = "teacher", qualifiedByName = "ForAppointment"),
            @Mapping(target = "student", qualifiedByName = "ForAppointment"),
            @Mapping(source = "createdAt", target = "createdAt"),
            @Mapping(source = "lastModifiedAt", target = "lastModifiedAt"),
            @Mapping(source = "organization", target = "organization"),
    })
    AppointmentDto appointmentToAppointmentDto(Appointment appointment);

    @Mappings({
            @Mapping(target = "appointmentId", expression = "java( appointment.getId().toString() )"),
            @Mapping(source = "appointmentName", target = "appointmentName"),
            @Mapping(source = "appointmentNote", target = "appointmentNote"),
            @Mapping(source = "appointmentStartDate", target = "appointmentStartDate"),
            @Mapping(source = "appointmentEndDate", target = "appointmentEndDate"),
            @Mapping(target = "teacher", qualifiedByName = "ForAppointment"),
            @Mapping(target = "student", qualifiedByName = "ForAppointment"),
            @Mapping(source = "createdAt", target = "createdAt"),
            @Mapping(source = "lastModifiedAt", target = "lastModifiedAt"),
            @Mapping(source = "organization", target = "organization"),
    })
    List<AppointmentDto> appointmentToAppointmentDto(List<Appointment> appointment);

}
