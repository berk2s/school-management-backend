package com.schoolplus.office.web.controllers.backoffice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolplus.office.domain.*;
import com.schoolplus.office.repository.AppointmentRepository;
import com.schoolplus.office.repository.GradeRepository;
import com.schoolplus.office.repository.TeachingSubjectRepository;
import com.schoolplus.office.repository.UserRepository;
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
    GradeRepository gradeRepository;

    @Autowired
    AppointmentRepository appointmentRepository;

    @DisplayName("Creating Appointment")
    @Nested
    class CreatingAppointment {

        CreatingAppointmentDto createAppointment;
        Student student;
        Teacher teacher;
        TeachingSubject teachingSubject;
        Grade grade;

        String APPOINTMENT_NAME;

        @BeforeEach
        void setUp() {
            student = new Student();
            student.setGradeType(GradeType.HIGH_SCHOOL);
            student.setGradeLevel(GradeLevel.ELEVENTH_GRADE);

            grade = new Grade();
            grade.setGradeType(GradeType.HIGH_SCHOOL);
            grade.setGradeLevel(GradeLevel.ELEVENTH_GRADE);
            grade.setGradeTag(RandomStringUtils.random(10, true, false));
            grade.addStudent(student);

            gradeRepository.save(grade);

            teachingSubject = new TeachingSubject();
            teachingSubject.setSubjectName("A Subject");

            teachingSubjectRepository.save(teachingSubject);

            teacher = new Teacher();
            teacher.setFirstName("Ayşe");
            teacher.setLastName("Gürbüz");
            teacher.setUsername(RandomStringUtils.random(10, true, false));
            teacher.addTeachingSubject(teachingSubject);

            userRepository.saveAll(List.of(student, teacher));

            createAppointment = new CreatingAppointmentDto();
            createAppointment.setAppointmentNote(RandomStringUtils.random(50, true, false));
            createAppointment.setAppointmentStartDate(LocalDateTime.of(2021, Month.AUGUST, 28, 15, 0));
            createAppointment.setAppointmentEndDate(LocalDateTime.of(2021, Month.AUGUST, 28, 15, 30));
            createAppointment.setStudentId(student.getId().toString());
            createAppointment.setTeacherId(teacher.getId().toString());

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
                    .andExpect(jsonPath("$.teacher.userId").isNotEmpty())
                    .andExpect(jsonPath("$.teacher.firstName", is(teacher.getFirstName())))
                    .andExpect(jsonPath("$.teacher.lastName", is(teacher.getLastName())))
                    .andExpect(jsonPath("$.teacher.teachingSubjects[*]..subjectName", anyOf(hasItem(is(teacher.getTeachingSubjects().get(0).getSubjectName())))))
                    .andExpect(jsonPath("$.student.userId").isNotEmpty())
                    .andExpect(jsonPath("$.student.firstName", is(student.getFirstName())))
                    .andExpect(jsonPath("$.student.lastName", is(student.getLastName())))
                    .andExpect(jsonPath("$.student.grade.gradeLevel", is(student.getGrade().getGradeLevel().getGradeYear())))
                    .andExpect(jsonPath("$.student.grade.gradeType", is(student.getGrade().getGradeType().getType())));

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

            teacher.addAppointment(newAppointment);

            appointmentRepository.save(newAppointment);

            createAppointment.setAppointmentStartDate(startDate);
            createAppointment.setAppointmentEndDate(endDate);

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

            student.addAppointment(newAppointment);

            appointmentRepository.save(newAppointment);

            createAppointment.setAppointmentStartDate(startDate);
            createAppointment.setAppointmentEndDate(endDate);

            mockMvc.perform(post(AppointmentManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createAppointment)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.STUDENT_NOT_AVAILABLE_FOR_APPOINTMENT.getDesc())));

        }

    }

    @DisplayName("Getting Appointment")
    @Nested
    class GettingAppointment {

        Appointment appointment;
        Student student;
        Teacher teacher;
        TeachingSubject teachingSubject;
        Grade grade;

        String APPOINTMENT_NAME;

        @BeforeEach
        void setUp() {
            appointment = new Appointment();
            appointment.setAppointmentNote(RandomStringUtils.random(50, true, false));
            appointment.setAppointmentStartDate(LocalDateTime.of(2021, Month.AUGUST, 28, 15, 0));
            appointment.setAppointmentEndDate(LocalDateTime.of(2021, Month.AUGUST, 28, 15, 30));
            appointment.setAppointmentName("Appointment name");
            student = new Student();
            student.setGradeType(GradeType.HIGH_SCHOOL);
            student.setGradeLevel(GradeLevel.ELEVENTH_GRADE);
            student.addAppointment(appointment);

            grade = new Grade();
            grade.setGradeType(GradeType.HIGH_SCHOOL);
            grade.setGradeLevel(GradeLevel.ELEVENTH_GRADE);
            grade.setGradeTag(RandomStringUtils.random(10, true, false));
            grade.addStudent(student);

            gradeRepository.save(grade);

            teachingSubject = new TeachingSubject();
            teachingSubject.setSubjectName("A Subject");

            teachingSubjectRepository.save(teachingSubject);

            teacher = new Teacher();
            teacher.setFirstName("Ayşe");
            teacher.setLastName("Gürbüz");
            teacher.setUsername(RandomStringUtils.random(10, true, false));
            teacher.addTeachingSubject(teachingSubject);
            teacher.addAppointment(appointment);

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
                    .andExpect(jsonPath("$..teacher.userId").isNotEmpty())
                    .andExpect(jsonPath("$..teacher.firstName", anyOf(hasItem(is(teacher.getFirstName())))))
                    .andExpect(jsonPath("$..teacher.lastName", anyOf(hasItem(is(teacher.getLastName())))))
                    .andExpect(jsonPath("$..teacher.teachingSubjects[*]..subjectName", anyOf(hasItem(is(teacher.getTeachingSubjects().get(0).getSubjectName())))))
                    .andExpect(jsonPath("$..student.userId").isNotEmpty())
                    .andExpect(jsonPath("$..student.firstName", anyOf(hasItem(is(student.getFirstName())))))
                    .andExpect(jsonPath("$..student.lastName", anyOf(hasItem(is(student.getLastName())))))
                    .andExpect(jsonPath("$..student.grade.gradeLevel", anyOf(hasItem(is(student.getGrade().getGradeLevel().getGradeYear())))))
                    .andExpect(jsonPath("$..student.grade.gradeType", anyOf(hasItem(is(student.getGrade().getGradeType().getType())))));


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
                    .andExpect(jsonPath("$.teacher.userId").isNotEmpty())
                    .andExpect(jsonPath("$.teacher.firstName", is(teacher.getFirstName())))
                    .andExpect(jsonPath("$.teacher.lastName", is(teacher.getLastName())))
                    .andExpect(jsonPath("$.teacher.teachingSubjects[*]..subjectName", anyOf(hasItem(is(teacher.getTeachingSubjects().get(0).getSubjectName())))))
                    .andExpect(jsonPath("$.student.userId").isNotEmpty())
                    .andExpect(jsonPath("$.student.firstName", is(student.getFirstName())))
                    .andExpect(jsonPath("$.student.lastName", is(student.getLastName())))
                    .andExpect(jsonPath("$.student.grade.gradeLevel", is(student.getGrade().getGradeLevel().getGradeYear())))
                    .andExpect(jsonPath("$.student.grade.gradeType", is(student.getGrade().getGradeType().getType())));


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
        Grade grade;

        String APPOINTMENT_NAME;

        @BeforeEach
        void setUp() {
            appointment = new Appointment();
            appointment.setAppointmentNote(RandomStringUtils.random(50, true, false));
            appointment.setAppointmentStartDate(LocalDateTime.of(2021, Month.AUGUST, 28, 15, 0));
            appointment.setAppointmentName("Appointment name");
            student = new Student();
            student.setGradeType(GradeType.HIGH_SCHOOL);
            student.setGradeLevel(GradeLevel.ELEVENTH_GRADE);
            student.addAppointment(appointment);

            grade = new Grade();
            grade.setGradeType(GradeType.HIGH_SCHOOL);
            grade.setGradeLevel(GradeLevel.ELEVENTH_GRADE);
            grade.setGradeTag(RandomStringUtils.random(10, true, false));
            grade.addStudent(student);

            gradeRepository.save(grade);

            teachingSubject = new TeachingSubject();
            teachingSubject.setSubjectName("A Subject");

            teachingSubjectRepository.save(teachingSubject);

            teacher = new Teacher();
            teacher.setFirstName("Ayşe");
            teacher.setLastName("Gürbüz");
            teacher.setUsername(RandomStringUtils.random(10, true, false));
            teacher.addTeachingSubject(teachingSubject);
            teacher.addAppointment(appointment);

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
        Grade grade;
        Student newStudent;
        Teacher newTeacher;
        EditingAppointmentDto editingAppointment;

        String APPOINTMENT_NAME;

        @BeforeEach
        void setUp() {
            appointment = new Appointment();
            appointment.setAppointmentNote(RandomStringUtils.random(50, true, false));
            appointment.setAppointmentStartDate(LocalDateTime.of(2021, Month.AUGUST, 28, 20, 0));
            appointment.setAppointmentEndDate(LocalDateTime.of(2021, Month.AUGUST, 28, 20, 30));
            appointment.setAppointmentName("Appointment name");

            student = new Student();
            student.setGradeType(GradeType.HIGH_SCHOOL);
            student.setGradeLevel(GradeLevel.ELEVENTH_GRADE);
            student.addAppointment(appointment);

            grade = new Grade();
            grade.setGradeType(GradeType.HIGH_SCHOOL);
            grade.setGradeLevel(GradeLevel.ELEVENTH_GRADE);
            grade.setGradeTag(RandomStringUtils.random(10, true, false));
            grade.addStudent(student);

            gradeRepository.save(grade);

            teachingSubject = new TeachingSubject();
            teachingSubject.setSubjectName("A Subject");

            teachingSubjectRepository.save(teachingSubject);

            teacher = new Teacher();
            teacher.setFirstName("Ayşe");
            teacher.setLastName("Gürbüz");
            teacher.setUsername(RandomStringUtils.random(10, true, false));
            teacher.addTeachingSubject(teachingSubject);
            teacher.addAppointment(appointment);

            userRepository.saveAll(List.of(student, teacher));
            appointmentRepository.save(appointment);

            newStudent = new Student();
            newStudent.setUsername(RandomStringUtils.random(10, true, false));
            newStudent.setGradeType(GradeType.HIGH_SCHOOL);
            newStudent.setGradeLevel(GradeLevel.ELEVENTH_GRADE);

            newTeacher = new Teacher();
            newTeacher.setFirstName(RandomStringUtils.random(10, true, false));
            newTeacher.setLastName(RandomStringUtils.random(10, true, false));
            newTeacher.setUsername(RandomStringUtils.random(10, true, false));
            newTeacher.addTeachingSubject(teachingSubject);

            userRepository.saveAll(List.of(newTeacher, newStudent));

            grade.addStudent(newStudent);

            gradeRepository.save(grade);

            editingAppointment = new EditingAppointmentDto();
            editingAppointment.setAppointmentName(RandomStringUtils.random(10, true, false));
            editingAppointment.setAppointmentNote(RandomStringUtils.random(50, true, false));
            editingAppointment.setAppointmentStartDate(LocalDateTime.of(2021, Month.AUGUST, 28, 19, 0));
            editingAppointment.setAppointmentEndDate(LocalDateTime.of(2021, Month.AUGUST, 28, 19, 30));
            editingAppointment.setStudentId(newStudent.getId().toString());
            editingAppointment.setTeacherId(newTeacher.getId().toString());

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
                    .andExpect(jsonPath("$.teacher.userId").isNotEmpty())
                    .andExpect(jsonPath("$.teacher.firstName", is(newTeacher.getFirstName())))
                    .andExpect(jsonPath("$.teacher.lastName", is(newTeacher.getLastName())))
                    .andExpect(jsonPath("$.teacher.teachingSubjects[*]..subjectName", anyOf(hasItem(is(newTeacher.getTeachingSubjects().get(0).getSubjectName())))))
                    .andExpect(jsonPath("$.student.userId").isNotEmpty())
                    .andExpect(jsonPath("$.student.firstName", is(newStudent.getFirstName())))
                    .andExpect(jsonPath("$.student.lastName", is(newStudent.getLastName())))
                    .andExpect(jsonPath("$.student.grade.gradeLevel", is(newStudent.getGrade().getGradeLevel().getGradeYear())))
                    .andExpect(jsonPath("$.student.grade.gradeType", is(newStudent.getGrade().getGradeType().getType())));


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

    }

}
