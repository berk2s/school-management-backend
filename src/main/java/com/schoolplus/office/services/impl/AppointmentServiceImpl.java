package com.schoolplus.office.services.impl;

import com.schoolplus.office.annotations.CreatingEntity;
import com.schoolplus.office.annotations.DeletingEntity;
import com.schoolplus.office.annotations.ReadingEntity;
import com.schoolplus.office.annotations.UpdatingEntity;
import com.schoolplus.office.domain.*;
import com.schoolplus.office.repository.AppointmentRepository;
import com.schoolplus.office.repository.OrganizationRepository;
import com.schoolplus.office.repository.UserRepository;
import com.schoolplus.office.services.AppointmentService;
import com.schoolplus.office.utils.AppointmentUtils;
import com.schoolplus.office.web.exceptions.*;
import com.schoolplus.office.web.mappers.AppointmentMapper;
import com.schoolplus.office.web.models.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final AppointmentMapper appointmentMapper;

    @ReadingEntity(domain = TransactionDomain.APPOINTMENT, action = DomainAction.READ_APPOINTMENT)
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:appointments') || hasAuthority('read:appointments'))")
    @Override
    public AppointmentDto getAppointment(UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> {
                   log.warn("Appointment with given id does not exists [appointmentId: {}]", appointmentId);
                   throw new AppointmentNotFoundException(ErrorDesc.APPOINTMENT_NOT_FOUND.getDesc());
                });

        return appointmentMapper.appointmentToAppointmentDto(appointment);
    }

    @ReadingEntity(domain = TransactionDomain.APPOINTMENT, action = DomainAction.READ_APPOINTMENTS, isList = true)
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:appointments') || hasAuthority('read:appointment'))")
    @Override
    public List<AppointmentDto> getAppointments(Pageable pageable) {
        Page<Appointment> appointments = appointmentRepository.findAll(pageable);
        return appointmentMapper.appointmentToAppointmentDto(appointments.getContent());
    }

    @CreatingEntity(domain = TransactionDomain.APPOINTMENT, action = DomainAction.CREATE_APPOINTMENT)
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:appointments') || hasAuthority('write:appointment'))")
    @Override
    public AppointmentDto createAppointment(CreatingAppointmentDto creatingAppointment) {
        LocalDateTime appointmentStartDate = creatingAppointment.getAppointmentStartDate();
        LocalDateTime appointmentEndDate = creatingAppointment.getAppointmentEndDate();

        Appointment appointment = new Appointment();
        appointment.setAppointmentStartDate(appointmentStartDate);
        appointment.setAppointmentEndDate(appointmentEndDate);

        if (creatingAppointment.getAppointmentNote() != null) {
            appointment.setAppointmentNote(creatingAppointment.getAppointmentNote());
        }

        Organization organization = organizationRepository.findById(creatingAppointment.getOrganizationId())
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists [organizationId: {}]", creatingAppointment.getOrganizationId());
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        appointment.setOrganization(organization);

        UUID teacherId = UUID.fromString(creatingAppointment.getTeacherId());
        UUID studentId = UUID.fromString(creatingAppointment.getStudentId());

        Teacher teacher = (Teacher) userRepository.findById(teacherId)
                .orElseThrow(() -> {
                    log.warn("Teacher with given id does not exists [teacherId: {}]", teacherId);
                    throw new TeacherNotFoundException(ErrorDesc.TEACHER_NOT_FOUND.getDesc());
                });

        Student student = (Student) userRepository.findById(studentId)
                .orElseThrow(() -> {
                    log.warn("Student with given id does not exists [studentId: {}]", studentId);
                    throw new StudentNotFoundException(ErrorDesc.STUDENT_NOT_FOUND.getDesc());
                });

        if(isAppointmentTaken(teacher,null, appointmentStartDate, appointmentEndDate))  {
            log.warn("The Teacher is not available between the start and end date of the taken appointment [teacherId: {}, startDate: {}, endDate: {}]",
                    teacherId, appointmentStartDate, appointmentEndDate);
            throw new AppointmentNotAvailableException(ErrorDesc.TEACHER_NOT_AVAILABLE_FOR_APPOINTMENT.getDesc());
        }

        if(isAppointmentTaken(student, null, appointmentStartDate, appointmentEndDate))  {
            log.warn("The Student is not available between the start and end date of the taken appointment [teacherId: {}, startDate: {}, endDate: {}]",
                    teacherId, appointmentStartDate, appointmentEndDate);
            throw new AppointmentNotAvailableException(ErrorDesc.STUDENT_NOT_AVAILABLE_FOR_APPOINTMENT.getDesc());
        }

        teacher.addAppointment(appointment);

        student.addAppointment(appointment);

        if (creatingAppointment.getAppointmentName() != null) {
            appointment.setAppointmentName(creatingAppointment.getAppointmentName());
        } else {
            appointment.setAppointmentName(AppointmentUtils.generateName(teacher.getFirstName(), teacher.getLastName()));
        }

        Appointment savedAppointment = appointmentRepository.save(appointment);

        log.info("The Appointment has been created successfully [appointmentId: {}, performedBy: {}]", savedAppointment.getId().toString(),
                SecurityContextHolder.getContext().getAuthentication().getName());

        return appointmentMapper.appointmentToAppointmentDto(savedAppointment);
    }

    @Transactional
    @UpdatingEntity(domain = TransactionDomain.APPOINTMENT, action = DomainAction.UPDATE_APPOINTMENT, idArg = "appointmentId")
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:appointments') || hasAuthority('update:appointment'))")
    @Override
    public void updateAppointment(UUID appointmentId, EditingAppointmentDto editingAppointment) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> {
                   log.warn("Appointment with given id does not exists [appointmentId: {}]", appointmentId);
                   throw new AppointmentNotFoundException(ErrorDesc.APPOINTMENT_NOT_FOUND.getDesc());
                });

        LocalDateTime startDate = editingAppointment.getAppointmentStartDate() != null ?
                editingAppointment.getAppointmentStartDate() : appointment.getAppointmentStartDate();

        LocalDateTime endDate = editingAppointment.getAppointmentEndDate() != null ?
                editingAppointment.getAppointmentEndDate() : appointment.getAppointmentEndDate();

        appointment.setAppointmentStartDate(startDate);
        appointment.setAppointmentEndDate(endDate);

        if (editingAppointment.getAppointmentName() != null)
            appointment.setAppointmentName(editingAppointment.getAppointmentName());

        if (editingAppointment.getAppointmentNote() != null)
            appointment.setAppointmentNote(editingAppointment.getAppointmentNote());

        if (editingAppointment.getOrganizationId() != null) {
            Organization organization = organizationRepository.findById(editingAppointment.getOrganizationId())
                            .orElseThrow(() -> {
                                log.warn("Organization with given id does not exists [organizationId: {}]", editingAppointment.getOrganizationId());
                                throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                            });

            appointment.setOrganization(organization);
        }

        if(editingAppointment.getTeacherId() != null
                && !editingAppointment.getTeacherId().equals(appointment.getTeacher().getId().toString())) {
            UUID teacherId = UUID.fromString(editingAppointment.getTeacherId());

            Teacher teacher = (Teacher) userRepository
                    .findById(teacherId)
                    .orElseThrow(() -> {
                        log.warn("Teacher with given id does not exists [teachetId: {}]", teacherId);
                        throw new TeacherNotFoundException(ErrorDesc.TEACHER_NOT_FOUND.getDesc());
                    });

            if (isAppointmentTaken(teacher, appointmentId,startDate, endDate)) {
                log.warn("The Teacher is not available between the start and end date of the taken appointment [teacherId: {}, startDate: {}, endDate: {}]",
                        teacherId, startDate, endDate);
                throw new AppointmentNotAvailableException(ErrorDesc.TEACHER_NOT_AVAILABLE_FOR_APPOINTMENT.getDesc());
            }

            teacher.addAppointment(appointment);
        } else {
            if (isAppointmentTaken(appointment.getTeacher(), appointmentId, startDate, endDate)) {
                log.warn("The Teacher is not available between the start and end date of the taken appointment [teacherId: {}, startDate: {}, endDate: {}]",
                        appointment.getTeacher().getId(), startDate, endDate);
                throw new AppointmentNotAvailableException(ErrorDesc.TEACHER_NOT_AVAILABLE_FOR_APPOINTMENT.getDesc());
            }
        }

        if (editingAppointment.getStudentId() != null
                && !editingAppointment.getStudentId().equals(appointment.getStudent().getId().toString())) {
            UUID studentId = UUID.fromString(editingAppointment.getStudentId());

            Student student = (Student) userRepository.findById(studentId)
                    .orElseThrow(() -> {
                        log.warn("Student with given id does not exists [studentId: {}]", studentId);
                        throw new StudentNotFoundException(ErrorDesc.STUDENT_NOT_FOUND.getDesc());
                    });

            if (isAppointmentTaken(student, appointmentId, startDate, endDate)) {
                log.warn("The Student is not available between the start and end date of the taken appointment [teacherId: {}, startDate: {}, endDate: {}]",
                        studentId, startDate, endDate);
                throw new AppointmentNotAvailableException(ErrorDesc.STUDENT_NOT_AVAILABLE_FOR_APPOINTMENT.getDesc());
            }

            student.addAppointment(appointment);
        } else {
            if (isAppointmentTaken(appointment.getStudent(), appointmentId, startDate, endDate)) {
                log.warn("The Student is not available between the start and end date of the taken appointment [teacherId: {}, startDate: {}, endDate: {}]",
                        appointment.getStudent().getId(), startDate, endDate);
                throw new AppointmentNotAvailableException(ErrorDesc.STUDENT_NOT_AVAILABLE_FOR_APPOINTMENT.getDesc());
            }
        }

        appointmentRepository.save(appointment);

        log.info("The Appointment has been edited successfully [appointmentId: {}, performedBy: {}]", appointment.getId().toString(),
                SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @DeletingEntity(domain = TransactionDomain.APPOINTMENT, action = DomainAction.DELETE_APPOINTMENT, idArg = "appointmentId")
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:appointments') || hasAuthority('delete:appointment'))")
    @Override
    public void deleteAppointment(UUID appointmentId) {
        if(!appointmentRepository.existsById(appointmentId)) {
            log.warn("Appointment with given id does not exists [appointmentId: {}]", appointmentId);
            throw new AppointmentNotFoundException(ErrorDesc.APPOINTMENT_NOT_FOUND.getDesc());
        }

        appointmentRepository.deleteById(appointmentId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:appointments') || hasAuthority('read:appointment'))")
    @Override
    public boolean isAppointmentTaken(CanAppointment t, UUID appointmentId, LocalDateTime start, LocalDateTime end) {
        Appointment appointment = new Appointment();
        appointment.setAppointmentStartDate(start);
        appointment.setAppointmentEndDate(end);

        if (t instanceof Teacher)
            appointment.setTeacher((Teacher) t);

        if (t instanceof Student)
            appointment.setStudent((Student) t);

        return appointmentRepository.exists(Example.of(appointment));
    }

}
