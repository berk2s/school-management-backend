package com.schoolplus.office.web.controllers.backoffice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolplus.office.domain.*;
import com.schoolplus.office.repository.*;
import com.schoolplus.office.web.models.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AppointmentManagementControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TeachingSubjectRepository teachingSubjectRepository;

    @Autowired
    ClassroomRepository classroomRepository;

    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    OrganizationRepository organizationRepository;

    @DisplayName("Creating Appointment")
    @Nested
    class CreatingAppointment {

        CreatingAppointmentDto createAppointment;
        Student student;
        Teacher teacher;
        TeachingSubject teachingSubject;
        Classroom classRoom;
        Organization organization;

        String APPOINTMENT_NAME;

        @BeforeEach
        void setUp() {
            organization = new Organization();
            organization.setOrganizationName(RandomStringUtils.random(10, true, false));

            organizationRepository.save(organization);

            student = new Student();
            student.setOrganization(organization);

            classRoom = new Classroom();
            classRoom.setClassRoomTag(RandomStringUtils.random(10, true, false));
            classRoom.addStudent(student);
            classRoom.setOrganization(organization);

            classroomRepository.save(classRoom);

            teachingSubject = new TeachingSubject();
            teachingSubject.setSubjectName("A Subject");
            teachingSubject.setOrganization(organization);

            teachingSubjectRepository.save(teachingSubject);

            teacher = new Teacher();
            teacher.setFirstName("Ayşe");
            teacher.setLastName("Gürbüz");
            teacher.setUsername(RandomStringUtils.random(10, true, false));
            teacher.addTeachingSubject(teachingSubject);
            teacher.setOrganization(organization);


            userRepository.saveAll(List.of(student, teacher));

            createAppointment = new CreatingAppointmentDto();
            createAppointment.setAppointmentNote(RandomStringUtils.random(50, true, false));
            createAppointment.setAppointmentStartDate(LocalDateTime.of(2021, Month.AUGUST, 28, 15, 0));
            createAppointment.setAppointmentEndDate(LocalDateTime.of(2021, Month.AUGUST, 28, 15, 30));
            createAppointment.setStudentId(student.getId().toString());
            createAppointment.setTeacherId(teacher.getId().toString());
            createAppointment.setOrganizationId(organization.getId());

            APPOINTMENT_NAME = teacher.getFirstName() + " " + teacher.getLastName() + " öğretmen ile randevu";
        }

        @DisplayName("Create Appointment Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:appointments"})
        @Test
        void createAppointmentSuccessfully() throws Exception {

            mockMvc.perform(post(AppointmentManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createAppointment)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.appointmentId").isNotEmpty())
                    .andExpect(jsonPath("$.appointmentName", is(APPOINTMENT_NAME)))
                    .andExpect(jsonPath("$.appointmentNote", is(createAppointment.getAppointmentNote())))
                    .andExpect(jsonPath("$.appointmentStartDate", is(createAppointment.getAppointmentStartDate().format(DateTimeFormatter.ISO_DATE_TIME))))
                    .andExpect(jsonPath("$.appointmentEndDate", is(createAppointment.getAppointmentEndDate().format(DateTimeFormatter.ISO_DATE_TIME))))
                    .andExpect(jsonPath("$.organization.organizationName", is(organization.getOrganizationName())))
                    .andExpect(jsonPath("$.teacher.userId").isNotEmpty())
                    .andExpect(jsonPath("$.teacher.firstName", is(teacher.getFirstName())))
                    .andExpect(jsonPath("$.teacher.lastName", is(teacher.getLastName())))
                    .andExpect(jsonPath("$.teacher.teachingSubjects[*]..subjectName", anyOf(hasItem(is(teacher.getTeachingSubjects().get(0).getSubjectName())))))
                    .andExpect(jsonPath("$.student.userId").isNotEmpty());

        }

        @DisplayName("Create Appointment Teacher Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:appointments"})
        @Test
        void createAppointmentTeacherNotFoundError() throws Exception {

            createAppointment.setTeacherId(UUID.randomUUID().toString());

            mockMvc.perform(post(AppointmentManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createAppointment)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.TEACHER_NOT_FOUND.getDesc())));

        }

        @DisplayName("Create Appointment Student Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:appointments"})
        @Test
        void createAppointmentStudentNotFoundError() throws Exception {

            createAppointment.setStudentId(UUID.randomUUID().toString());

            mockMvc.perform(post(AppointmentManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createAppointment)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.STUDENT_NOT_FOUND.getDesc())));

        }

        @DisplayName("Create Appointment Teacher Not Available Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:appointments"})
        @Test
        void createAppointmentTeacherNotAvailableError() throws Exception {

            var startDate = LocalDateTime.of(2021, Month.AUGUST, 28, 16, 0);
            var endDate= LocalDateTime.of(2021, Month.AUGUST, 28, 16, 30);

            Appointment newAppointment = new Appointment();
            newAppointment.setAppointmentStartDate(startDate);
            newAppointment.setAppointmentEndDate(endDate);
            newAppointment.setOrganization(organization);

            teacher.addAppointment(newAppointment);

            appointmentRepository.save(newAppointment);

            createAppointment.setAppointmentStartDate(startDate);
            createAppointment.setAppointmentEndDate(endDate);
            createAppointment.setOrganizationId(organization.getId());

            mockMvc.perform(post(AppointmentManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createAppointment)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.TEACHER_NOT_AVAILABLE_FOR_APPOINTMENT.getDesc())));

        }

        @DisplayName("Create Appointment Student Not Available Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:appointments"})
        @Test
        void createAppointmentStudentNotAvailableError() throws Exception {

            var startDate = LocalDateTime.of(2021, Month.AUGUST, 28, 16, 0);
            var endDate= LocalDateTime.of(2021, Month.AUGUST, 28, 16, 30);

            Appointment newAppointment = new Appointment();
            newAppointment.setAppointmentStartDate(startDate);
            newAppointment.setAppointmentEndDate(endDate);
            newAppointment.setOrganization(organization);

            student.addAppointment(newAppointment);

            appointmentRepository.save(newAppointment);

            createAppointment.setAppointmentStartDate(startDate);
            createAppointment.setAppointmentEndDate(endDate);
            createAppointment.setOrganizationId(organization.getId());

            mockMvc.perform(post(AppointmentManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createAppointment)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.STUDENT_NOT_AVAILABLE_FOR_APPOINTMENT.getDesc())));

        }

        @DisplayName("Create Appointment Organization Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:appointments"})
        @Test
        void createAppointmentOrganizationNotFoundError() throws Exception {

            createAppointment.setOrganizationId(12312312312L);

            mockMvc.perform(post(AppointmentManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createAppointment)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));

        }


    }

    @DisplayName("Getting Appointment")
    @Nested
    class GettingAppointment {

        Appointment appointment;
        Student student;
        Teacher teacher;
        TeachingSubject teachingSubject;
        Classroom classRoom;
        Organization organization;

        String APPOINTMENT_NAME;

        @BeforeEach
        void setUp() {
            organization = new Organization();
            organization.setOrganizationName(RandomStringUtils.random(10, true, false));

            organizationRepository.save(organization);

            appointment = new Appointment();
            appointment.setAppointmentNote(RandomStringUtils.random(50, true, false));
            appointment.setAppointmentStartDate(LocalDateTime.of(2021, Month.AUGUST, 28, 15, 0));
            appointment.setAppointmentEndDate(LocalDateTime.of(2021, Month.AUGUST, 28, 15, 30));
            appointment.setAppointmentName("Appointment name");
            appointment.setOrganization(organization);

            student = new Student();
            student.addAppointment(appointment);
            student.setOrganization(organization);

            classRoom = new Classroom();
            classRoom.setClassRoomTag(RandomStringUtils.random(10, true, false));
            classRoom.addStudent(student);
            classRoom.setOrganization(organization);

            classroomRepository.save(classRoom);

            teachingSubject = new TeachingSubject();
            teachingSubject.setSubjectName("A Subject");
            teachingSubject.setOrganization(organization);

            teachingSubjectRepository.save(teachingSubject);

            teacher = new Teacher();
            teacher.setFirstName("Ayşe");
            teacher.setLastName("Gürbüz");
            teacher.setUsername(RandomStringUtils.random(10, true, false));
            teacher.addTeachingSubject(teachingSubject);
            teacher.addAppointment(appointment);
            teacher.setOrganization(organization);

            userRepository.saveAll(List.of(student, teacher));
            appointmentRepository.save(appointment);

            APPOINTMENT_NAME = teacher.getFirstName() + " " + teacher.getLastName() + " öğretmen ile randevu";
        }

        @DisplayName("Get Appointments Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:appointments"})
        @Test
        void getAppointmentsSuccessfully() throws Exception {

            mockMvc.perform(get(AppointmentManagementController.ENDPOINT + "?page=0&size=10"))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..appointmentId").isNotEmpty())
                    .andExpect(jsonPath("$..appointmentName").isNotEmpty())
                    .andExpect(jsonPath("$..appointmentNote", anyOf(hasItem(is(appointment.getAppointmentNote())))))
                    .andExpect(jsonPath("$..appointmentStartDate", anyOf(hasItem(is(appointment.getAppointmentStartDate().format(DateTimeFormatter.ISO_DATE_TIME))))))
                    .andExpect(jsonPath("$..appointmentEndDate", anyOf(hasItem(is(appointment.getAppointmentEndDate().format(DateTimeFormatter.ISO_DATE_TIME))))))
                    .andExpect(jsonPath("$..organization.organizationName").isNotEmpty())
                    .andExpect(jsonPath("$..teacher.userId").isNotEmpty())
                    .andExpect(jsonPath("$..teacher.firstName", anyOf(hasItem(is(teacher.getFirstName())))))
                    .andExpect(jsonPath("$..teacher.lastName", anyOf(hasItem(is(teacher.getLastName())))))
                    .andExpect(jsonPath("$..teacher.teachingSubjects[*]..subjectName", anyOf(hasItem(is(teacher.getTeachingSubjects().get(0).getSubjectName())))))
                    .andExpect(jsonPath("$..student.userId").isNotEmpty());

        }

        @DisplayName("Get Appointment Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:appointments"})
        @Test
        void getAppointmentSuccessfully() throws Exception {

            mockMvc.perform(get(AppointmentManagementController.ENDPOINT + "/" + appointment.getId()))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.appointmentId").isNotEmpty())
                    .andExpect(jsonPath("$.appointmentName").isNotEmpty())
                    .andExpect(jsonPath("$.appointmentNote", is(appointment.getAppointmentNote())))
                    .andExpect(jsonPath("$.appointmentStartDate", is(appointment.getAppointmentStartDate().format(DateTimeFormatter.ISO_DATE_TIME))))
                    .andExpect(jsonPath("$.appointmentEndDate", is(appointment.getAppointmentEndDate().format(DateTimeFormatter.ISO_DATE_TIME))))
                    .andExpect(jsonPath("$.organization.organizationName", is(organization.getOrganizationName())))
                    .andExpect(jsonPath("$.teacher.userId").isNotEmpty())
                    .andExpect(jsonPath("$.teacher.firstName", is(teacher.getFirstName())))
                    .andExpect(jsonPath("$.teacher.lastName", is(teacher.getLastName())))
                    .andExpect(jsonPath("$.teacher.teachingSubjects[*]..subjectName", anyOf(hasItem(is(teacher.getTeachingSubjects().get(0).getSubjectName())))))
                    .andExpect(jsonPath("$.student.userId").isNotEmpty());
        }

        @DisplayName("Get Appointment Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:appointments"})
        @Test
        void getAppointmentNotFoundError() throws Exception {

            mockMvc.perform(get(AppointmentManagementController.ENDPOINT + "/" + UUID.randomUUID()))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.APPOINTMENT_NOT_FOUND.getDesc())));

        }

    }

    @DisplayName("Deleting Appointment")
    @Nested
    class DeletingAppointment {

        Appointment appointment;
        Student student;
        Teacher teacher;
        TeachingSubject teachingSubject;
        Classroom classRoom;
        Organization organization;

        String APPOINTMENT_NAME;

        @BeforeEach
        void setUp() {
            organization = new Organization();

            organizationRepository.save(organization);

            appointment = new Appointment();
            appointment.setAppointmentNote(RandomStringUtils.random(50, true, false));
            appointment.setAppointmentStartDate(LocalDateTime.of(2021, Month.AUGUST, 28, 15, 0));
            appointment.setAppointmentName("Appointment name");
            appointment.setOrganization(organization);

            student = new Student();
            student.addAppointment(appointment);
            student.setOrganization(organization);

            classRoom = new Classroom();
            classRoom.setClassRoomTag(RandomStringUtils.random(10, true, false));
            classRoom.addStudent(student);
            classRoom.setOrganization(organization);

            classroomRepository.save(classRoom);

            teachingSubject = new TeachingSubject();
            teachingSubject.setSubjectName("A Subject");
            teachingSubject.setOrganization(organization);

            teachingSubjectRepository.save(teachingSubject);

            teacher = new Teacher();
            teacher.setFirstName("Ayşe");
            teacher.setLastName("Gürbüz");
            teacher.setUsername(RandomStringUtils.random(10, true, false));
            teacher.addTeachingSubject(teachingSubject);
            teacher.addAppointment(appointment);
            teacher.setOrganization(organization);

            userRepository.saveAll(List.of(student, teacher));
            appointmentRepository.save(appointment);

            APPOINTMENT_NAME = teacher.getFirstName() + " " + teacher.getLastName() + " öğretmen ile randevu";
        }

        @DisplayName("Delete Appointment Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:appointments"})
        @Test
        void deleteAppointmentSuccessfully() throws Exception {

            mockMvc.perform(delete(AppointmentManagementController.ENDPOINT + "/" + appointment.getId().toString()))
                    .andDo(print())
                    .andExpect(status().isNoContent());

        }

        @DisplayName("Delete Appointment Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:appointments"})
        @Test
        void deleteAppointmentNotFoundError() throws Exception {

            mockMvc.perform(delete(AppointmentManagementController.ENDPOINT + "/" + UUID.randomUUID()))
                    .andDo(print())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.APPOINTMENT_NOT_FOUND.getDesc())));

        }


    }

    @Transactional
    @DisplayName("Editing Appointment")
    @Nested
    class EditingAppointment {

        Appointment appointment;
        Student student;
        Teacher teacher;
        TeachingSubject teachingSubject;
        Classroom classRoom;
        Student newStudent;
        Teacher newTeacher;
        EditingAppointmentDto editingAppointment;
        Organization organization;
        Organization newOrganization;

        String APPOINTMENT_NAME;

        @BeforeEach
        void setUp() {
            organization = new Organization();
            organization.setOrganizationName(RandomStringUtils.random(10, true, false));

            newOrganization = new Organization();
            newOrganization.setOrganizationName(RandomStringUtils.random(10, true, false));

            organizationRepository.saveAll(List.of(organization, newOrganization));

            appointment = new Appointment();
            appointment.setAppointmentNote(RandomStringUtils.random(50, true, false));
            appointment.setAppointmentStartDate(LocalDateTime.of(2021, Month.AUGUST, 28, 20, 0));
            appointment.setAppointmentEndDate(LocalDateTime.of(2021, Month.AUGUST, 28, 20, 30));
            appointment.setAppointmentName("Appointment name");
            appointment.setOrganization(organization);

            student = new Student();
            student.addAppointment(appointment);
            student.setOrganization(organization);

            classRoom = new Classroom();
            classRoom.setClassRoomTag(RandomStringUtils.random(10, true, false));
            classRoom.addStudent(student);
            classRoom.setOrganization(organization);

            classroomRepository.save(classRoom);

            teachingSubject = new TeachingSubject();
            teachingSubject.setSubjectName("A Subject");
            teachingSubject.setOrganization(organization);

            teachingSubjectRepository.save(teachingSubject);

            teacher = new Teacher();
            teacher.setFirstName("Ayşe");
            teacher.setLastName("Gürbüz");
            teacher.setUsername(RandomStringUtils.random(10, true, false));
            teacher.addTeachingSubject(teachingSubject);
            teacher.addAppointment(appointment);
            teacher.setOrganization(organization);

            userRepository.saveAll(List.of(student, teacher));
            appointmentRepository.save(appointment);

            newStudent = new Student();
            newStudent.setUsername(RandomStringUtils.random(10, true, false));

            newStudent.setOrganization(organization);

            newTeacher = new Teacher();
            newTeacher.setFirstName(RandomStringUtils.random(10, true, false));
            newTeacher.setLastName(RandomStringUtils.random(10, true, false));
            newTeacher.setUsername(RandomStringUtils.random(10, true, false));
            newTeacher.addTeachingSubject(teachingSubject);
            newTeacher.setOrganization(organization);

            userRepository.saveAll(List.of(newTeacher, newStudent));

            classRoom.addStudent(newStudent);

            classroomRepository.save(classRoom);



            editingAppointment = new EditingAppointmentDto();
            editingAppointment.setAppointmentName(RandomStringUtils.random(10, true, false));
            editingAppointment.setAppointmentNote(RandomStringUtils.random(50, true, false));
            editingAppointment.setAppointmentStartDate(LocalDateTime.of(2021, Month.AUGUST, 28, 19, 0));
            editingAppointment.setAppointmentEndDate(LocalDateTime.of(2021, Month.AUGUST, 28, 19, 30));
            editingAppointment.setStudentId(newStudent.getId().toString());
            editingAppointment.setTeacherId(newTeacher.getId().toString());
            editingAppointment.setOrganizationId(newOrganization.getId());

            APPOINTMENT_NAME = teacher.getFirstName() + " " + teacher.getLastName() + " öğretmen ile randevu";
        }

        @DisplayName("Edit Appointment Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:appointments"})
        @Test
        void editAppointmentSuccessfully() throws Exception {

            mockMvc.perform(put(AppointmentManagementController.ENDPOINT + "/" + appointment.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingAppointment)))
                    .andDo(print())
                    .andExpect(status().isPermanentRedirect())
                    .andExpect(redirectedUrl(AppointmentManagementController.ENDPOINT + "/" + appointment.getId()));

            mockMvc.perform(get(AppointmentManagementController.ENDPOINT + "/" + appointment.getId()))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.appointmentId").isNotEmpty())
                    .andExpect(jsonPath("$.appointmentName", is(editingAppointment.getAppointmentName())))
                    .andExpect(jsonPath("$.appointmentNote", is(editingAppointment.getAppointmentNote())))
                    .andExpect(jsonPath("$.appointmentStartDate", is(editingAppointment.getAppointmentStartDate().format(DateTimeFormatter.ISO_DATE_TIME))))
                    .andExpect(jsonPath("$.appointmentEndDate", is(editingAppointment.getAppointmentEndDate().format(DateTimeFormatter.ISO_DATE_TIME))))
                    .andExpect(jsonPath("$.organization.organizationName", is(newOrganization.getOrganizationName())))
                    .andExpect(jsonPath("$.teacher.userId").isNotEmpty())
                    .andExpect(jsonPath("$.teacher.firstName", is(newTeacher.getFirstName())))
                    .andExpect(jsonPath("$.teacher.lastName", is(newTeacher.getLastName())))
                    .andExpect(jsonPath("$.teacher.teachingSubjects[*]..subjectName", anyOf(hasItem(is(newTeacher.getTeachingSubjects().get(0).getSubjectName())))))
                    .andExpect(jsonPath("$.student.userId").isNotEmpty());


        }

        @DisplayName("Edit Appointment Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:appointments"})
        @Test
        void editAppointmentNotFoundError() throws Exception {
            mockMvc.perform(put(AppointmentManagementController.ENDPOINT + "/" + UUID.randomUUID().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingAppointment)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.APPOINTMENT_NOT_FOUND.getDesc())));
        }

        @DisplayName("Edit Appointment New Teacher Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:appointments"})
        @Test
        void editAppointmentNewTeacherNotFoundError() throws Exception {
            editingAppointment.setTeacherId(UUID.randomUUID().toString());

            mockMvc.perform(put(AppointmentManagementController.ENDPOINT + "/" + appointment.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingAppointment)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.TEACHER_NOT_FOUND.getDesc())));
        }

        @DisplayName("Edit Appointment New Teacher Is Not Available Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:appointments"})
        @Test
        void editAppointmentNewTeacherIsNotAvailableError() throws Exception {
            Appointment newAppointment = new Appointment();
            newAppointment.setAppointmentNote(RandomStringUtils.random(50, true, false));
            newAppointment.setAppointmentStartDate(LocalDateTime.of(2021, Month.AUGUST, 28, 15, 0));
            newAppointment.setAppointmentEndDate(LocalDateTime.of(2021, Month.AUGUST, 28, 15, 30));
            newAppointment.setAppointmentName("Appointment name");
            newAppointment.setOrganization(organization);

            appointmentRepository.save(newAppointment);

            newTeacher.addAppointment(newAppointment);

            editingAppointment.setAppointmentStartDate(newAppointment.getAppointmentStartDate());
            editingAppointment.setAppointmentEndDate(newAppointment.getAppointmentEndDate());

            mockMvc.perform(put(AppointmentManagementController.ENDPOINT + "/" + appointment.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingAppointment)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.TEACHER_NOT_AVAILABLE_FOR_APPOINTMENT.getDesc())));
        }

        @DisplayName("Edit Appointment Current Teacher Is Not Available Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:appointments"})
        @Test
        void editAppointmentCurrentTeacherIsNotAvailableError() throws Exception {
            Appointment newAppointment = new Appointment();
            newAppointment.setAppointmentNote(RandomStringUtils.random(50, true, false));
            newAppointment.setAppointmentStartDate(LocalDateTime.of(2021, Month.AUGUST, 28, 15, 0));
            newAppointment.setAppointmentEndDate(LocalDateTime.of(2021, Month.AUGUST, 28, 15, 30));
            newAppointment.setAppointmentName("Appointment name");

            teacher.addAppointment(newAppointment);

            editingAppointment.setAppointmentStartDate(newAppointment.getAppointmentStartDate());
            editingAppointment.setAppointmentEndDate(newAppointment.getAppointmentEndDate());
            editingAppointment.setTeacherId(null);

            mockMvc.perform(put(AppointmentManagementController.ENDPOINT + "/" + appointment.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingAppointment)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.TEACHER_NOT_AVAILABLE_FOR_APPOINTMENT.getDesc())));
        }

        @DisplayName("Edit Appointment New Student Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:appointments"})
        @Test
        void editAppointmentNewStudentNotFoundError() throws Exception {
            editingAppointment.setStudentId(UUID.randomUUID().toString());
            mockMvc.perform(put(AppointmentManagementController.ENDPOINT + "/" + appointment.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingAppointment)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.STUDENT_NOT_FOUND.getDesc())));
        }

        @DisplayName("Edit Appointment New Student Is Not Available Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:appointments"})
        @Test
        void editAppointmentNewStudentIsNotAvailableError() throws Exception {
            Appointment newAppointment = new Appointment();
            newAppointment.setAppointmentNote(RandomStringUtils.random(50, true, false));
            newAppointment.setAppointmentStartDate(LocalDateTime.of(2021, Month.AUGUST, 28, 15, 0));
            newAppointment.setAppointmentEndDate(LocalDateTime.of(2021, Month.AUGUST, 28, 15, 30));
            newAppointment.setAppointmentName("Appointment name for new student");
            newAppointment.setOrganization(organization);

            appointmentRepository.save(newAppointment);

            student.addAppointment(newAppointment);

            editingAppointment.setAppointmentStartDate(newAppointment.getAppointmentStartDate());
            editingAppointment.setAppointmentEndDate(newAppointment.getAppointmentEndDate());
            editingAppointment.setStudentId(student.getId().toString());

            mockMvc.perform(put(AppointmentManagementController.ENDPOINT + "/" + appointment.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingAppointment)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.STUDENT_NOT_AVAILABLE_FOR_APPOINTMENT.getDesc())));
        }

        @DisplayName("Edit Appointment Current Student Is Not Available Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:appointments"})
        @Test
        void editAppointmentCurrentStudentIsNotAvailableError() throws Exception {
            Appointment newAppointment = new Appointment();
            newAppointment.setAppointmentNote(RandomStringUtils.random(50, true, false));
            newAppointment.setAppointmentStartDate(LocalDateTime.of(2021, Month.AUGUST, 28, 15, 0));
            newAppointment.setAppointmentEndDate(LocalDateTime.of(2021, Month.AUGUST, 28, 15, 30));
            newAppointment.setAppointmentName("Appointment name");

            student.addAppointment(newAppointment);

            editingAppointment.setAppointmentStartDate(newAppointment.getAppointmentStartDate());
            editingAppointment.setAppointmentEndDate(newAppointment.getAppointmentEndDate());
            editingAppointment.setStudentId(null);

            mockMvc.perform(put(AppointmentManagementController.ENDPOINT + "/" + appointment.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingAppointment)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.STUDENT_NOT_AVAILABLE_FOR_APPOINTMENT.getDesc())));
        }

        @DisplayName("Edit Appointment Organization Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:appointments"})
        @Test
        void editAppointmentOrganizationNotFoundError() throws Exception {
            editingAppointment.setOrganizationId(12312312312L);
            mockMvc.perform(put(AppointmentManagementController.ENDPOINT + "/" + appointment.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingAppointment)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));
        }


    }

}
