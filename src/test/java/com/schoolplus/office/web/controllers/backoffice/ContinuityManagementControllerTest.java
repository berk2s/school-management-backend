package com.schoolplus.office.web.controllers.backoffice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolplus.office.domain.*;
import com.schoolplus.office.repository.*;
import com.schoolplus.office.web.models.CreatingContinuityDto;
import com.schoolplus.office.web.models.EditingContinuityDto;
import com.schoolplus.office.web.models.ErrorDesc;
import com.schoolplus.office.web.models.ErrorType;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ContinuityManagementControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    SyllabusRepository syllabusRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LessonRepository lessonRepository;

    @Autowired
    GradeRepository gradeRepository;

    @Autowired
    ClassroomRepository classroomRepository;

    @Autowired
    ContinuityRepository continuityRepository;

    Organization organization;
    Teacher teacher;
    Student student;
    Lesson lesson;
    Classroom classroom;
    Grade grade;
    Syllabus syllabus;
    Continuity continuity;

    @BeforeEach
    void setUp() {
        organization = new Organization();
        organization.setOrganizationName(RandomStringUtils.random(10, true, false));

        organizationRepository.save(organization);

        teacher = new Teacher();
        teacher.setUsername(RandomStringUtils.random(10, true, false));
        teacher.setFirstName(RandomStringUtils.random(10, true, false));
        teacher.setLastName(RandomStringUtils.random(10, true, false));
        teacher.setOrganization(organization);

        student = new Student();
        student.setUsername(RandomStringUtils.random(10, true, false));
        student.setFirstName(RandomStringUtils.random(10, true, false));
        student.setLastName(RandomStringUtils.random(10, true, false));
        student.setOrganization(organization);

        userRepository.saveAll(List.of(student, teacher));

        lesson = new Lesson();
        lesson.setLessonName(RandomStringUtils.random(10, true, false));

        lessonRepository.save(lesson);

        grade = new Grade();
        grade.setOrganization(organization);

        gradeRepository.save(grade);

        classroom = new Classroom();
        classroom.setClassRoomTag(RandomStringUtils.random(10,true,false));
        classroom.setOrganization(organization);
        classroom.setGrade(grade);
        classroom.setAdvisorTeacher(teacher);

        classroomRepository.save(classroom);

        syllabus = new Syllabus();
        syllabus.setClassroom(classroom);
        syllabus.setLesson(lesson);
        syllabus.setTeacher(teacher);
        syllabus.setOrganization(organization);
        syllabus.setSyllabusNote(RandomStringUtils.random(10, true, false));
        syllabus.setSyllabusStartDate(LocalDateTime.now().plusMinutes(10));
        syllabus.setSyllabusEndDate(LocalDateTime.now().plusMinutes(70));

        syllabusRepository.save(syllabus);

        continuity = new Continuity();
        continuity.setStudent(student);
        continuity.setSyllabus(syllabus);
        continuity.setClassroom(classroom);
        continuity.setOrganization(organization);
        continuity.setIsAbsent(true);

        continuityRepository.save(continuity);
    }

    @DisplayName("Creating Continuity")
    @Nested
    class CreatingContinuity {

        CreatingContinuityDto creatingContinuity;

        @BeforeEach
        void setUp() {
            creatingContinuity = new CreatingContinuityDto();
            creatingContinuity.setClassroomId(classroom.getId());
            creatingContinuity.setStudentId(student.getId().toString());
            creatingContinuity.setSyllabusId(syllabus.getId());
            creatingContinuity.setOrganizationId(organization.getId());
            creatingContinuity.setIsAbsent(true);
        }

        @Transactional
        @DisplayName("Create Continuity Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:continuities"})
        @Test
        void createContinuitySuccessfully() throws Exception {
            mockMvc.perform(post(ContinuityManagementController.ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(creatingContinuity)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.continuityId").isNotEmpty())
                    .andExpect(jsonPath("$.isAbsent", is(creatingContinuity.getIsAbsent())))
                    .andExpect(jsonPath("$.syllabus.syllabusId", is(syllabus.getId().intValue())))
                    .andExpect(jsonPath("$.syllabus.syllabusStartDate", is(syllabus.getSyllabusStartDate().format(DateTimeFormatter.ISO_DATE_TIME))))
                    .andExpect(jsonPath("$.syllabus.syllabusEndDate", is(syllabus.getSyllabusEndDate().format(DateTimeFormatter.ISO_DATE_TIME))))
                    .andExpect(jsonPath("$.classroom.classRoomId", is(classroom.getId().intValue())))
                    .andExpect(jsonPath("$.student.userId", is(student.getId().toString())))
                    .andExpect(jsonPath("$.student.firstName", is(student.getFirstName())))
                    .andExpect(jsonPath("$.student.lastName", is(student.getLastName())))
                    .andExpect(jsonPath("$.organization.organizationId", is(organization.getId().intValue())));
        }

        @DisplayName("Create Continuity Syllabus Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:continuities"})
        @Test
        void createContinuitySyllabusNotFoundError() throws Exception {
            creatingContinuity.setSyllabusId(1231232L);
            mockMvc.perform(post(ContinuityManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(creatingContinuity)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.SYLLABUS_NOT_FOUND.getDesc())));
        }

        @DisplayName("Create Continuity Classroom Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:continuities"})
        @Test
        void createContinuityClassroomNotFoundError() throws Exception {
            creatingContinuity.setClassroomId(12312312L);
            mockMvc.perform(post(ContinuityManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(creatingContinuity)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc())));
        }

        @DisplayName("Create Continuity Student Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:continuities"})
        @Test
        void createContinuityStudentNotFoundError() throws Exception {
            creatingContinuity.setStudentId(UUID.randomUUID().toString());
            mockMvc.perform(post(ContinuityManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(creatingContinuity)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.STUDENT_NOT_FOUND.getDesc())));
        }


        @DisplayName("Create Continuity Organization Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:continuities"})
        @Test
        void createContinuityOrganizationNotFoundError() throws Exception {
            creatingContinuity.setOrganizationId(12312312L);
            mockMvc.perform(post(ContinuityManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(creatingContinuity)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));
        }
    }

    @DisplayName("Getting Continuity")
    @Nested
    class GettingContinuity {

        @DisplayName("Get Continuity Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:continuities"})
        @Test
        void getContinuitySuccessfully() throws Exception {
            mockMvc.perform(get(ContinuityManagementController.ENDPOINT + "/" + continuity.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.continuityId").isNotEmpty())
                    .andExpect(jsonPath("$.isAbsent", is(continuity.getIsAbsent())))
                    .andExpect(jsonPath("$.syllabus.syllabusId", is(syllabus.getId().intValue())))
                    .andExpect(jsonPath("$.syllabus.syllabusStartDate", is(syllabus.getSyllabusStartDate().format(DateTimeFormatter.ISO_DATE_TIME))))
                    .andExpect(jsonPath("$.syllabus.syllabusEndDate", is(syllabus.getSyllabusEndDate().format(DateTimeFormatter.ISO_DATE_TIME))))
                    .andExpect(jsonPath("$.classroom.classRoomId", is(classroom.getId().intValue())))
                    .andExpect(jsonPath("$.student.userId", is(student.getId().toString())))
                    .andExpect(jsonPath("$.student.firstName", is(student.getFirstName())))
                    .andExpect(jsonPath("$.student.lastName", is(student.getLastName())))
                    .andExpect(jsonPath("$.organization.organizationId", is(organization.getId().intValue())))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());
        }

        @DisplayName("Get Continuity By Syllabus Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:continuities"})
        @Test
        void getContinuityBySyllabusSuccessfully() throws Exception {
            mockMvc.perform(get(ContinuityManagementController.ENDPOINT + "/syllabus/" + syllabus.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..continuityId").isNotEmpty())
                    .andExpect(jsonPath("$..isAbsent", anyOf(hasItem(is(continuity.getIsAbsent())))))
                    .andExpect(jsonPath("$..syllabus.syllabusId", anyOf(hasItem(is(syllabus.getId().intValue())))))
                    .andExpect(jsonPath("$..syllabus.syllabusStartDate", anyOf(hasItem(is(syllabus.getSyllabusStartDate().format(DateTimeFormatter.ISO_DATE_TIME))))))
                    .andExpect(jsonPath("$..syllabus.syllabusEndDate", anyOf(hasItem(is(syllabus.getSyllabusEndDate().format(DateTimeFormatter.ISO_DATE_TIME))))))
                    .andExpect(jsonPath("$..classroom.classRoomId", anyOf(hasItem(is(classroom.getId().intValue())))))
                    .andExpect(jsonPath("$..student.userId", anyOf(hasItem(is(student.getId().toString())))))
                    .andExpect(jsonPath("$..student.firstName", anyOf(hasItem(is(student.getFirstName())))))
                    .andExpect(jsonPath("$..student.lastName", anyOf(hasItem(is(student.getLastName())))))
                    .andExpect(jsonPath("$..organization.organizationId", anyOf(hasItem(is(organization.getId().intValue())))))
                    .andExpect(jsonPath("$..createdAt").isNotEmpty())
                    .andExpect(jsonPath("$..lastModifiedAt").isNotEmpty());
        }

        @DisplayName("Get Continuity By Classroom Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:continuities"})
        @Test
        void getContinuityByClassroomSuccessfully() throws Exception {
            mockMvc.perform(get(ContinuityManagementController.ENDPOINT + "/classroom/" + classroom.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..continuityId").isNotEmpty())
                    .andExpect(jsonPath("$..isAbsent", anyOf(hasItem(is(continuity.getIsAbsent())))))
                    .andExpect(jsonPath("$..syllabus.syllabusId", anyOf(hasItem(is(syllabus.getId().intValue())))))
                    .andExpect(jsonPath("$..syllabus.syllabusStartDate", anyOf(hasItem(is(syllabus.getSyllabusStartDate().format(DateTimeFormatter.ISO_DATE_TIME))))))
                    .andExpect(jsonPath("$..syllabus.syllabusEndDate", anyOf(hasItem(is(syllabus.getSyllabusEndDate().format(DateTimeFormatter.ISO_DATE_TIME))))))
                    .andExpect(jsonPath("$..classroom.classRoomId", anyOf(hasItem(is(classroom.getId().intValue())))))
                    .andExpect(jsonPath("$..student.userId", anyOf(hasItem(is(student.getId().toString())))))
                    .andExpect(jsonPath("$..student.firstName", anyOf(hasItem(is(student.getFirstName())))))
                    .andExpect(jsonPath("$..student.lastName", anyOf(hasItem(is(student.getLastName())))))
                    .andExpect(jsonPath("$..organization.organizationId", anyOf(hasItem(is(organization.getId().intValue())))))
                    .andExpect(jsonPath("$..createdAt").isNotEmpty())
                    .andExpect(jsonPath("$..lastModifiedAt").isNotEmpty());
        }

        @DisplayName("Get Continuity By Student Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:continuities"})
        @Test
        void getContinuityByStudentSuccessfully() throws Exception {
            mockMvc.perform(get(ContinuityManagementController.ENDPOINT + "/student/" + student.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..continuityId").isNotEmpty())
                    .andExpect(jsonPath("$..isAbsent", anyOf(hasItem(is(continuity.getIsAbsent())))))
                    .andExpect(jsonPath("$..syllabus.syllabusId", anyOf(hasItem(is(syllabus.getId().intValue())))))
                    .andExpect(jsonPath("$..syllabus.syllabusStartDate", anyOf(hasItem(is(syllabus.getSyllabusStartDate().format(DateTimeFormatter.ISO_DATE_TIME))))))
                    .andExpect(jsonPath("$..syllabus.syllabusEndDate", anyOf(hasItem(is(syllabus.getSyllabusEndDate().format(DateTimeFormatter.ISO_DATE_TIME))))))
                    .andExpect(jsonPath("$..classroom.classRoomId", anyOf(hasItem(is(classroom.getId().intValue())))))
                    .andExpect(jsonPath("$..student.userId", anyOf(hasItem(is(student.getId().toString())))))
                    .andExpect(jsonPath("$..student.firstName", anyOf(hasItem(is(student.getFirstName())))))
                    .andExpect(jsonPath("$..student.lastName", anyOf(hasItem(is(student.getLastName())))))
                    .andExpect(jsonPath("$..organization.organizationId", anyOf(hasItem(is(organization.getId().intValue())))))
                    .andExpect(jsonPath("$..createdAt").isNotEmpty())
                    .andExpect(jsonPath("$..lastModifiedAt").isNotEmpty());
        }

        @DisplayName("Get Continuity By Organization Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:continuities"})
        @Test
        void getContinuityByOrganizationSuccessfully() throws Exception {
            mockMvc.perform(get(ContinuityManagementController.ENDPOINT + "/organization/" + organization.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..continuityId").isNotEmpty())
                    .andExpect(jsonPath("$..isAbsent", anyOf(hasItem(is(continuity.getIsAbsent())))))
                    .andExpect(jsonPath("$..syllabus.syllabusId", anyOf(hasItem(is(syllabus.getId().intValue())))))
                    .andExpect(jsonPath("$..syllabus.syllabusStartDate", anyOf(hasItem(is(syllabus.getSyllabusStartDate().format(DateTimeFormatter.ISO_DATE_TIME))))))
                    .andExpect(jsonPath("$..syllabus.syllabusEndDate", anyOf(hasItem(is(syllabus.getSyllabusEndDate().format(DateTimeFormatter.ISO_DATE_TIME))))))
                    .andExpect(jsonPath("$..classroom.classRoomId", anyOf(hasItem(is(classroom.getId().intValue())))))
                    .andExpect(jsonPath("$..student.userId", anyOf(hasItem(is(student.getId().toString())))))
                    .andExpect(jsonPath("$..student.firstName", anyOf(hasItem(is(student.getFirstName())))))
                    .andExpect(jsonPath("$..student.lastName", anyOf(hasItem(is(student.getLastName())))))
                    .andExpect(jsonPath("$..organization.organizationId", anyOf(hasItem(is(organization.getId().intValue())))))
                    .andExpect(jsonPath("$..createdAt").isNotEmpty())
                    .andExpect(jsonPath("$..lastModifiedAt").isNotEmpty());
        }

        @DisplayName("Get Continuity Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:continuities"})
        @Test
        void getContinuityNotFoundError() throws Exception {
            mockMvc.perform(get(ContinuityManagementController.ENDPOINT + "/" + UUID.randomUUID()))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.CONTINUITY_NOT_FOUND.getDesc())));
        }

        @DisplayName("Get Continuity Syllabus Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:continuities"})
        @Test
        void getContinuitySyllabusNotFoundError() throws Exception {
            mockMvc.perform(get(ContinuityManagementController.ENDPOINT + "/syllabus/12312312312"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.SYLLABUS_NOT_FOUND.getDesc())));
        }

        @DisplayName("Get Continuity Classroom Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:continuities"})
        @Test
        void getContinuityClassroomNotFoundError() throws Exception {
            mockMvc.perform(get(ContinuityManagementController.ENDPOINT + "/classroom/12312312312"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc())));
        }

        @DisplayName("Get Continuity Student Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:continuities"})
        @Test
        void getContinuityStudentNotFoundError() throws Exception {
            mockMvc.perform(get(ContinuityManagementController.ENDPOINT + "/student/" + UUID.randomUUID().toString()))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.STUDENT_NOT_FOUND.getDesc())));
        }

        @DisplayName("Get Continuity Organization Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:continuities"})
        @Test
        void getContinuityOrganizationNotFoundError() throws Exception {
            mockMvc.perform(get(ContinuityManagementController.ENDPOINT + "/organization/12312312"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));
        }

    }

    @DisplayName("Editing Continuity")
    @Nested
    class EditingContinuity {

        EditingContinuityDto editingContinuity;
        Teacher newTeacher;
        Student newStudent;
        Lesson newLesson;
        Classroom newClassroom;
        Grade newGrade;
        Syllabus newSyllabus;

        @BeforeEach
        void setUp() {
            newTeacher = new Teacher();
            newTeacher.setUsername(RandomStringUtils.random(10, true, false));
            newTeacher.setFirstName(RandomStringUtils.random(10, true, false));
            newTeacher.setLastName(RandomStringUtils.random(10, true, false));
            newTeacher.setOrganization(organization);

            newStudent = new Student();
            newStudent.setUsername(RandomStringUtils.random(10, true, false));
            newStudent.setFirstName(RandomStringUtils.random(10, true, false));
            newStudent.setLastName(RandomStringUtils.random(10, true, false));
            newStudent.setOrganization(organization);

            userRepository.saveAll(List.of(newStudent, newTeacher));

            newLesson = new Lesson();
            newLesson.setLessonName(RandomStringUtils.random(10, true, false));

            lessonRepository.save(newLesson);

            newGrade = new Grade();
            newGrade.setOrganization(organization);

            gradeRepository.save(newGrade);

            newClassroom = new Classroom();
            newClassroom.setClassRoomTag(RandomStringUtils.random(10,true,false));
            newClassroom.setOrganization(organization);
            newClassroom.setGrade(newGrade);
            newClassroom.setAdvisorTeacher(newTeacher);

            classroomRepository.save(newClassroom);

            newSyllabus = new Syllabus();
            newSyllabus.setClassroom(newClassroom);
            newSyllabus.setLesson(newLesson);
            newSyllabus.setTeacher(newTeacher);
            newSyllabus.setOrganization(organization);
            newSyllabus.setSyllabusNote(RandomStringUtils.random(10, true, false));
            newSyllabus.setSyllabusStartDate(LocalDateTime.now().plusMinutes(10));
            newSyllabus.setSyllabusEndDate(LocalDateTime.now().plusMinutes(70));

            syllabusRepository.save(newSyllabus);

            editingContinuity = new EditingContinuityDto();
            editingContinuity.setClassroomId(newClassroom.getId());
            editingContinuity.setStudentId(newStudent.getId().toString());
            editingContinuity.setSyllabusId(newSyllabus.getId());
            editingContinuity.setIsAbsent(false);
        }

        @DisplayName("Edit Continuity Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:continuities"})
        @Test
        void editContinuitySuccessfully() throws Exception {
            mockMvc.perform(put(ContinuityManagementController.ENDPOINT + "/" + continuity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(editingContinuity)))
                    .andExpect(status().isPermanentRedirect())
                    .andExpect(redirectedUrl(ContinuityManagementController.ENDPOINT + "/" + continuity.getId()));

            mockMvc.perform(get(ContinuityManagementController.ENDPOINT + "/" + continuity.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.continuityId").isNotEmpty())
                    .andExpect(jsonPath("$.isAbsent", is(editingContinuity.getIsAbsent())))
                    .andExpect(jsonPath("$.syllabus.syllabusId", is(newSyllabus.getId().intValue())))
                    .andExpect(jsonPath("$.syllabus.syllabusStartDate", is(newSyllabus.getSyllabusStartDate().format(DateTimeFormatter.ISO_DATE_TIME))))
                    .andExpect(jsonPath("$.syllabus.syllabusEndDate", is(newSyllabus.getSyllabusEndDate().format(DateTimeFormatter.ISO_DATE_TIME))))
                    .andExpect(jsonPath("$.classroom.classRoomId", is(newClassroom.getId().intValue())))
                    .andExpect(jsonPath("$.student.userId", is(newStudent.getId().toString())))
                    .andExpect(jsonPath("$.student.firstName", is(newStudent.getFirstName())))
                    .andExpect(jsonPath("$.student.lastName", is(newStudent.getLastName())))
                    .andExpect(jsonPath("$.organization.organizationId", is(organization.getId().intValue())))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());
        }

        @DisplayName("Edit Continuity Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:continuities"})
        @Test
        void editContinuityNotFoundError() throws Exception {
            mockMvc.perform(put(ContinuityManagementController.ENDPOINT + "/" + UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(editingContinuity)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.CONTINUITY_NOT_FOUND.getDesc())));
        }

        @DisplayName("Edit Continuity Syllabus Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:continuities"})
        @Test
        void editContinuitySyllabusNotFoundError() throws Exception {
            editingContinuity.setSyllabusId(123123123L)
            ;
            mockMvc.perform(put(ContinuityManagementController.ENDPOINT + "/" + continuity.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingContinuity)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.SYLLABUS_NOT_FOUND.getDesc())));
        }

        @DisplayName("Edit Continuity Classroom Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:continuities"})
        @Test
        void editContinuityClassroomNotFoundError() throws Exception {
            editingContinuity.setClassroomId(123123123L);
            ;
            mockMvc.perform(put(ContinuityManagementController.ENDPOINT + "/" + continuity.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingContinuity)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc())));
        }

        @DisplayName("Edit Continuity Student Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:continuities"})
        @Test
        void editContinuityStudentNotFoundError() throws Exception {
            editingContinuity.setStudentId(UUID.randomUUID().toString());

            mockMvc.perform(put(ContinuityManagementController.ENDPOINT + "/" + continuity.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingContinuity)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.STUDENT_NOT_FOUND.getDesc())));
        }

        @DisplayName("Edit Continuity Organization Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:continuities"})
        @Test
        void editContinuityOrganizationNotFoundError() throws Exception {
            editingContinuity.setOrganizationId(12312312312L);

            mockMvc.perform(put(ContinuityManagementController.ENDPOINT + "/" + continuity.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingContinuity)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));
        }

    }

    @DisplayName("Deleting Continuity")
    @Nested
    class DeletingContinuity {

        @DisplayName("Delete Continuity Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:continuities"})
        @Test
        void deleteContinuitySuccessfully() throws Exception {
            mockMvc.perform(delete(ContinuityManagementController.ENDPOINT + "/" + continuity.getId()))
                    .andExpect(status().isNoContent());
        }

        @DisplayName("Delete Continuity Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:continuities"})
        @Test
        void deleteContinuityNotFoundError() throws Exception {
            mockMvc.perform(delete(ContinuityManagementController.ENDPOINT + "/" + UUID.randomUUID()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.CONTINUITY_NOT_FOUND.getDesc())));
        }

    }

}
