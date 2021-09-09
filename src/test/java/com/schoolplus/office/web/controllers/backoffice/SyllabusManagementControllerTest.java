package com.schoolplus.office.web.controllers.backoffice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolplus.office.domain.*;
import com.schoolplus.office.repository.*;
import com.schoolplus.office.web.models.CreatingSyllabusDto;
import com.schoolplus.office.web.models.EditingSyllabusDto;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SyllabusManagementControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    LessonRepository lessonRepository;

    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    GradeRepository gradeRepository;

    @Autowired
    ClassroomRepository classroomRepository;

    @Autowired
    SyllabusRepository syllabusRepository;

    Organization organization;
    Teacher teacher;
    Lesson lesson;
    Classroom classroom;
    Grade grade;
    Syllabus syllabus;

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

        userRepository.save(teacher);

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
    }

    @DisplayName("Creating Syllabuses")
    @Nested
    class CreatingSyllabuses {

        CreatingSyllabusDto creatingSyllabus;

        @BeforeEach
        void setUp() {
            creatingSyllabus = new CreatingSyllabusDto();
            creatingSyllabus.setSyllabusNote(RandomStringUtils.random(10, true, false));
            creatingSyllabus.setLessonId(lesson.getId());
            creatingSyllabus.setTeacherId(teacher.getId().toString());
            creatingSyllabus.setOrganizationId(organization.getId());
            creatingSyllabus.setClassroomId(classroom.getId());
            creatingSyllabus.setSyllabusStartDate(LocalDateTime.of(2021, 9, 10, 13,20));
            creatingSyllabus.setSyllabusEndDate(LocalDateTime.of(2021, 9, 10, 14,0));
        }

        @DisplayName("Create Syllabus Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:syllabuses"})
        @Test
        void createSyllabusSuccessfully() throws Exception {

            mockMvc.perform(post(SyllabusManagementController.ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(creatingSyllabus)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.syllabusId").isNotEmpty())
                    .andExpect(jsonPath("$.syllabusNote", is(creatingSyllabus.getSyllabusNote())))
                    .andExpect(jsonPath("$.classroom.classRoomId", is(classroom.getId().intValue())))
                    .andExpect(jsonPath("$.classroom.classRoomTag", is(classroom.getClassRoomTag())))
                    .andExpect(jsonPath("$.classroom.advisorTeacher.userId", is(teacher.getId().toString())))
                    .andExpect(jsonPath("$.classroom.advisorTeacher.firstName", is(teacher.getFirstName())))
                    .andExpect(jsonPath("$.classroom.advisorTeacher.lastName", is(teacher.getLastName())))
                    .andExpect(jsonPath("$.classroom.grade.gradeId", is(grade.getId().intValue())))
                    .andExpect(jsonPath("$.teacher.userId", is(teacher.getId().toString())))
                    .andExpect(jsonPath("$.teacher.firstName", is(teacher.getFirstName())))
                    .andExpect(jsonPath("$.teacher.lastName", is(teacher.getLastName())))
                    .andExpect(jsonPath("$.lesson.lessonId", is(lesson.getId().intValue())))
                    .andExpect(jsonPath("$.lesson.lessonName", is(lesson.getLessonName())))
                    .andExpect(jsonPath("$.organization.organizationId", is(organization.getId().intValue())))
                    .andExpect(jsonPath("$.organization.organizationName", is(organization.getOrganizationName())))
                    .andExpect(jsonPath("$.syllabusStartDate", is(creatingSyllabus.getSyllabusStartDate().format(DateTimeFormatter.ISO_DATE_TIME).toString())))
                    .andExpect(jsonPath("$.syllabusEndDate", is(creatingSyllabus.getSyllabusEndDate().format(DateTimeFormatter.ISO_DATE_TIME).toString())));

        }

        @DisplayName("Create Syllabus Classroom Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:syllabuses"})
        @Test
        void createSyllabusClassroomNotFoundError() throws Exception {
            creatingSyllabus.setClassroomId(123123L);

            mockMvc.perform(post(SyllabusManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingSyllabus)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc())));
        }


        @DisplayName("Create Syllabus Lesson Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:syllabuses"})
        @Test
        void createSyllabusLessonNotFoundError() throws Exception {
            creatingSyllabus.setLessonId(123123L);

            mockMvc.perform(post(SyllabusManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingSyllabus)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.LESSON_NOT_FOUND.getDesc())));
        }

        @DisplayName("Create Syllabus Teacher Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:syllabuses"})
        @Test
        void createSyllabusTeacherNotFoundError() throws Exception {
            creatingSyllabus.setTeacherId(UUID.randomUUID().toString());

            mockMvc.perform(post(SyllabusManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingSyllabus)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.TEACHER_NOT_FOUND.getDesc())));
        }

        @DisplayName("Create Syllabus Organization Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:syllabuses"})
        @Test
        void createSyllabusOrganizationNotFoundError() throws Exception {
            creatingSyllabus.setOrganizationId(123123123L);

            mockMvc.perform(post(SyllabusManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingSyllabus)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));
        }

    }

    @DisplayName("Getting Syllabuses")
    @Nested
    class GettingSyllabuses {

        @DisplayName("Get Syllabus Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:syllabuses"})
        @Test
        void getSyllabusSuccessfully() throws Exception {
            mockMvc.perform(get(SyllabusManagementController.ENDPOINT + "/" + syllabus.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.syllabusId", is(syllabus.getId().intValue())))
                    .andExpect(jsonPath("$.syllabusNote", is(syllabus.getSyllabusNote())))
                    .andExpect(jsonPath("$.classroom.classRoomId", is(classroom.getId().intValue())))
                    .andExpect(jsonPath("$.classroom.classRoomTag", is(classroom.getClassRoomTag())))
                    .andExpect(jsonPath("$.classroom.advisorTeacher.userId", is(teacher.getId().toString())))
                    .andExpect(jsonPath("$.classroom.advisorTeacher.firstName", is(teacher.getFirstName())))
                    .andExpect(jsonPath("$.classroom.advisorTeacher.lastName", is(teacher.getLastName())))
                    .andExpect(jsonPath("$.classroom.grade.gradeId", is(grade.getId().intValue())))
                    .andExpect(jsonPath("$.teacher.userId", is(teacher.getId().toString())))
                    .andExpect(jsonPath("$.teacher.firstName", is(teacher.getFirstName())))
                    .andExpect(jsonPath("$.teacher.lastName", is(teacher.getLastName())))
                    .andExpect(jsonPath("$.lesson.lessonId", is(lesson.getId().intValue())))
                    .andExpect(jsonPath("$.lesson.lessonName", is(lesson.getLessonName())))
                    .andExpect(jsonPath("$.organization.organizationId", is(organization.getId().intValue())))
                    .andExpect(jsonPath("$.organization.organizationName", is(organization.getOrganizationName())))
                    .andExpect(jsonPath("$.syllabusStartDate", is(syllabus.getSyllabusStartDate().format(DateTimeFormatter.ISO_DATE_TIME).toString())))
                    .andExpect(jsonPath("$.syllabusEndDate", is(syllabus.getSyllabusEndDate().format(DateTimeFormatter.ISO_DATE_TIME).toString())));

        }

        @DisplayName("Get Syllabuses Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:syllabuses"})
        @Test
        void getSyllabusesSuccessfully() throws Exception {
            mockMvc.perform(get(SyllabusManagementController.ENDPOINT + "?size=1000"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..syllabusId", anyOf(hasItem(is(syllabus.getId().intValue())))))
                    .andExpect(jsonPath("$..syllabusNote", anyOf(hasItem(is(syllabus.getSyllabusNote())))))
                    .andExpect(jsonPath("$..classroom.classRoomId", anyOf(hasItem(is(classroom.getId().intValue())))))
                    .andExpect(jsonPath("$..classroom.classRoomTag", anyOf(hasItem(is(classroom.getClassRoomTag())))))
                    .andExpect(jsonPath("$..classroom.advisorTeacher.userId", anyOf(hasItem(is(teacher.getId().toString())))))
                    .andExpect(jsonPath("$..classroom.advisorTeacher.firstName", anyOf(hasItem(is(teacher.getFirstName())))))
                    .andExpect(jsonPath("$..classroom.advisorTeacher.lastName", anyOf(hasItem(is(teacher.getLastName())))))
                    .andExpect(jsonPath("$..classroom.grade.gradeId", anyOf(hasItem(is(grade.getId().intValue())))))
                    .andExpect(jsonPath("$..teacher.userId", anyOf(hasItem(is(teacher.getId().toString())))))
                    .andExpect(jsonPath("$..teacher.firstName", anyOf(hasItem(is(teacher.getFirstName())))))
                    .andExpect(jsonPath("$..teacher.lastName", anyOf(hasItem(is(teacher.getLastName())))))
                    .andExpect(jsonPath("$..lesson.lessonId", anyOf(hasItem(is(lesson.getId().intValue())))))
                    .andExpect(jsonPath("$..lesson.lessonName", anyOf(hasItem(is(lesson.getLessonName())))))
                    .andExpect(jsonPath("$..organization.organizationId", anyOf(hasItem(is(organization.getId().intValue())))))
                    .andExpect(jsonPath("$..organization.organizationName", anyOf(hasItem(is(organization.getOrganizationName())))))
                    .andExpect(jsonPath("$..syllabusStartDate", anyOf(hasItem(is(syllabus.getSyllabusStartDate().format(DateTimeFormatter.ISO_DATE_TIME).toString())))))
                    .andExpect(jsonPath("$..syllabusEndDate", anyOf(hasItem(is(syllabus.getSyllabusEndDate().format(DateTimeFormatter.ISO_DATE_TIME).toString())))));

        }

        @DisplayName("Get Syllabuses By Classroom Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:syllabuses"})
        @Test
        void getSyllabusesByClassroomSuccessfully() throws Exception {
            mockMvc.perform(get(SyllabusManagementController.ENDPOINT + "/classroom/" + classroom.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..syllabusId", anyOf(hasItem(is(syllabus.getId().intValue())))))
                    .andExpect(jsonPath("$..syllabusNote", anyOf(hasItem(is(syllabus.getSyllabusNote())))))
                    .andExpect(jsonPath("$..classroom.classRoomId", anyOf(hasItem(is(classroom.getId().intValue())))))
                    .andExpect(jsonPath("$..classroom.classRoomTag", anyOf(hasItem(is(classroom.getClassRoomTag())))))
                    .andExpect(jsonPath("$..classroom.advisorTeacher.userId", anyOf(hasItem(is(teacher.getId().toString())))))
                    .andExpect(jsonPath("$..classroom.advisorTeacher.firstName", anyOf(hasItem(is(teacher.getFirstName())))))
                    .andExpect(jsonPath("$..classroom.advisorTeacher.lastName", anyOf(hasItem(is(teacher.getLastName())))))
                    .andExpect(jsonPath("$..classroom.grade.gradeId", anyOf(hasItem(is(grade.getId().intValue())))))
                    .andExpect(jsonPath("$..teacher.userId", anyOf(hasItem(is(teacher.getId().toString())))))
                    .andExpect(jsonPath("$..teacher.firstName", anyOf(hasItem(is(teacher.getFirstName())))))
                    .andExpect(jsonPath("$..teacher.lastName", anyOf(hasItem(is(teacher.getLastName())))))
                    .andExpect(jsonPath("$..lesson.lessonId", anyOf(hasItem(is(lesson.getId().intValue())))))
                    .andExpect(jsonPath("$..lesson.lessonName", anyOf(hasItem(is(lesson.getLessonName())))))
                    .andExpect(jsonPath("$..organization.organizationId", anyOf(hasItem(is(organization.getId().intValue())))))
                    .andExpect(jsonPath("$..organization.organizationName", anyOf(hasItem(is(organization.getOrganizationName())))))
                    .andExpect(jsonPath("$..syllabusStartDate", anyOf(hasItem(is(syllabus.getSyllabusStartDate().format(DateTimeFormatter.ISO_DATE_TIME).toString())))))
                    .andExpect(jsonPath("$..syllabusEndDate", anyOf(hasItem(is(syllabus.getSyllabusEndDate().format(DateTimeFormatter.ISO_DATE_TIME).toString())))));

        }

        @DisplayName("Get Syllabuses By Lesson Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:syllabuses"})
        @Test
        void getSyllabusesByLessonSuccessfully() throws Exception {
            mockMvc.perform(get(SyllabusManagementController.ENDPOINT + "/lesson/" + lesson.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..syllabusId", anyOf(hasItem(is(syllabus.getId().intValue())))))
                    .andExpect(jsonPath("$..syllabusNote", anyOf(hasItem(is(syllabus.getSyllabusNote())))))
                    .andExpect(jsonPath("$..classroom.classRoomId", anyOf(hasItem(is(classroom.getId().intValue())))))
                    .andExpect(jsonPath("$..classroom.classRoomTag", anyOf(hasItem(is(classroom.getClassRoomTag())))))
                    .andExpect(jsonPath("$..classroom.advisorTeacher.userId", anyOf(hasItem(is(teacher.getId().toString())))))
                    .andExpect(jsonPath("$..classroom.advisorTeacher.firstName", anyOf(hasItem(is(teacher.getFirstName())))))
                    .andExpect(jsonPath("$..classroom.advisorTeacher.lastName", anyOf(hasItem(is(teacher.getLastName())))))
                    .andExpect(jsonPath("$..classroom.grade.gradeId", anyOf(hasItem(is(grade.getId().intValue())))))
                    .andExpect(jsonPath("$..teacher.userId", anyOf(hasItem(is(teacher.getId().toString())))))
                    .andExpect(jsonPath("$..teacher.firstName", anyOf(hasItem(is(teacher.getFirstName())))))
                    .andExpect(jsonPath("$..teacher.lastName", anyOf(hasItem(is(teacher.getLastName())))))
                    .andExpect(jsonPath("$..lesson.lessonId", anyOf(hasItem(is(lesson.getId().intValue())))))
                    .andExpect(jsonPath("$..lesson.lessonName", anyOf(hasItem(is(lesson.getLessonName())))))
                    .andExpect(jsonPath("$..organization.organizationId", anyOf(hasItem(is(organization.getId().intValue())))))
                    .andExpect(jsonPath("$..organization.organizationName", anyOf(hasItem(is(organization.getOrganizationName())))))
                    .andExpect(jsonPath("$..syllabusStartDate", anyOf(hasItem(is(syllabus.getSyllabusStartDate().format(DateTimeFormatter.ISO_DATE_TIME).toString())))))
                    .andExpect(jsonPath("$..syllabusEndDate", anyOf(hasItem(is(syllabus.getSyllabusEndDate().format(DateTimeFormatter.ISO_DATE_TIME).toString())))));

        }

        @DisplayName("Get Syllabuses By Teacher Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:syllabuses"})
        @Test
        void getSyllabusesByTeacherSuccessfully() throws Exception {
            mockMvc.perform(get(SyllabusManagementController.ENDPOINT + "/teacher/" + teacher.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..syllabusId", anyOf(hasItem(is(syllabus.getId().intValue())))))
                    .andExpect(jsonPath("$..syllabusNote", anyOf(hasItem(is(syllabus.getSyllabusNote())))))
                    .andExpect(jsonPath("$..classroom.classRoomId", anyOf(hasItem(is(classroom.getId().intValue())))))
                    .andExpect(jsonPath("$..classroom.classRoomTag", anyOf(hasItem(is(classroom.getClassRoomTag())))))
                    .andExpect(jsonPath("$..classroom.advisorTeacher.userId", anyOf(hasItem(is(teacher.getId().toString())))))
                    .andExpect(jsonPath("$..classroom.advisorTeacher.firstName", anyOf(hasItem(is(teacher.getFirstName())))))
                    .andExpect(jsonPath("$..classroom.advisorTeacher.lastName", anyOf(hasItem(is(teacher.getLastName())))))
                    .andExpect(jsonPath("$..classroom.grade.gradeId", anyOf(hasItem(is(grade.getId().intValue())))))
                    .andExpect(jsonPath("$..teacher.userId", anyOf(hasItem(is(teacher.getId().toString())))))
                    .andExpect(jsonPath("$..teacher.firstName", anyOf(hasItem(is(teacher.getFirstName())))))
                    .andExpect(jsonPath("$..teacher.lastName", anyOf(hasItem(is(teacher.getLastName())))))
                    .andExpect(jsonPath("$..lesson.lessonId", anyOf(hasItem(is(lesson.getId().intValue())))))
                    .andExpect(jsonPath("$..lesson.lessonName", anyOf(hasItem(is(lesson.getLessonName())))))
                    .andExpect(jsonPath("$..organization.organizationId", anyOf(hasItem(is(organization.getId().intValue())))))
                    .andExpect(jsonPath("$..organization.organizationName", anyOf(hasItem(is(organization.getOrganizationName())))))
                    .andExpect(jsonPath("$..syllabusStartDate", anyOf(hasItem(is(syllabus.getSyllabusStartDate().format(DateTimeFormatter.ISO_DATE_TIME).toString())))))
                    .andExpect(jsonPath("$..syllabusEndDate", anyOf(hasItem(is(syllabus.getSyllabusEndDate().format(DateTimeFormatter.ISO_DATE_TIME).toString())))));

        }

        @DisplayName("Get Syllabuses By Organization Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:syllabuses"})
        @Test
        void getSyllabusesByOrganizationSuccessfully() throws Exception {
            mockMvc.perform(get(SyllabusManagementController.ENDPOINT + "/organization/" + organization.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..syllabusId", anyOf(hasItem(is(syllabus.getId().intValue())))))
                    .andExpect(jsonPath("$..syllabusNote", anyOf(hasItem(is(syllabus.getSyllabusNote())))))
                    .andExpect(jsonPath("$..classroom.classRoomId", anyOf(hasItem(is(classroom.getId().intValue())))))
                    .andExpect(jsonPath("$..classroom.classRoomTag", anyOf(hasItem(is(classroom.getClassRoomTag())))))
                    .andExpect(jsonPath("$..classroom.advisorTeacher.userId", anyOf(hasItem(is(teacher.getId().toString())))))
                    .andExpect(jsonPath("$..classroom.advisorTeacher.firstName", anyOf(hasItem(is(teacher.getFirstName())))))
                    .andExpect(jsonPath("$..classroom.advisorTeacher.lastName", anyOf(hasItem(is(teacher.getLastName())))))
                    .andExpect(jsonPath("$..classroom.grade.gradeId", anyOf(hasItem(is(grade.getId().intValue())))))
                    .andExpect(jsonPath("$..teacher.userId", anyOf(hasItem(is(teacher.getId().toString())))))
                    .andExpect(jsonPath("$..teacher.firstName", anyOf(hasItem(is(teacher.getFirstName())))))
                    .andExpect(jsonPath("$..teacher.lastName", anyOf(hasItem(is(teacher.getLastName())))))
                    .andExpect(jsonPath("$..lesson.lessonId", anyOf(hasItem(is(lesson.getId().intValue())))))
                    .andExpect(jsonPath("$..lesson.lessonName", anyOf(hasItem(is(lesson.getLessonName())))))
                    .andExpect(jsonPath("$..organization.organizationId", anyOf(hasItem(is(organization.getId().intValue())))))
                    .andExpect(jsonPath("$..organization.organizationName", anyOf(hasItem(is(organization.getOrganizationName())))))
                    .andExpect(jsonPath("$..syllabusStartDate", anyOf(hasItem(is(syllabus.getSyllabusStartDate().format(DateTimeFormatter.ISO_DATE_TIME).toString())))))
                    .andExpect(jsonPath("$..syllabusEndDate", anyOf(hasItem(is(syllabus.getSyllabusEndDate().format(DateTimeFormatter.ISO_DATE_TIME).toString())))));

        }

        @DisplayName("Get Syllabuses By Classroom Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:syllabuses"})
        @Test
        void getSyllabusesByClassroomNotFoundError() throws Exception {
            mockMvc.perform(get(SyllabusManagementController.ENDPOINT + "/classroom/123123123"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc())));
        }

        @DisplayName("Get Syllabuses By Lesson Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:syllabuses"})
        @Test
        void getSyllabusesByLessonNotFoundError() throws Exception {
            mockMvc.perform(get(SyllabusManagementController.ENDPOINT + "/lesson/123123123"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.LESSON_NOT_FOUND.getDesc())));
        }

        @DisplayName("Get Syllabuses By Teacher Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:syllabuses"})
        @Test
        void getSyllabusesByTeacherNotFoundError() throws Exception {
            mockMvc.perform(get(SyllabusManagementController.ENDPOINT + "/teacher/" + UUID.randomUUID().toString()))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.TEACHER_NOT_FOUND.getDesc())));
        }

        @DisplayName("Get Syllabuses By Organization Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:syllabuses"})
        @Test
        void getSyllabusesByOrganizationNotFoundError() throws Exception {
            mockMvc.perform(get(SyllabusManagementController.ENDPOINT + "/organization/123123"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));
        }

        @DisplayName("Get Syllabus Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:syllabuses"})
        @Test
        void getSyllabusNotFoundError() throws Exception {
            mockMvc.perform(get(SyllabusManagementController.ENDPOINT + "/123123123"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.SYLLABUS_NOT_FOUND.getDesc())));
        }

    }

    @DisplayName("Editing Syllabuses")
    @Nested
    class EditingSyllabuses {

        EditingSyllabusDto editingSyllabuses;
        Teacher newTeacher;
        Lesson newLesson;
        Classroom newClassroom;

        @BeforeEach
        void setUp() {
            newTeacher = new Teacher();
            newTeacher.setUsername(RandomStringUtils.random(10, true, false));
            newTeacher.setFirstName(RandomStringUtils.random(10, true, false));
            newTeacher.setLastName(RandomStringUtils.random(10, true, false));
            newTeacher.setOrganization(organization);

            userRepository.save(newTeacher);

            newLesson = new Lesson();
            newLesson.setLessonName(RandomStringUtils.random(10, true, false));

            lessonRepository.save(newLesson);

            newClassroom = new Classroom();
            newClassroom.setClassRoomTag(RandomStringUtils.random(10,true,false));
            newClassroom.setOrganization(organization);
            newClassroom.setGrade(grade);
            newClassroom.setAdvisorTeacher(teacher);

            classroomRepository.save(newClassroom);

            editingSyllabuses = new EditingSyllabusDto();
            editingSyllabuses.setSyllabusNote(RandomStringUtils.random(10, true, false));
            editingSyllabuses.setClassroomId(newClassroom.getId());
            editingSyllabuses.setOrganizationId(organization.getId());
            editingSyllabuses.setTeacherId(newTeacher.getId().toString());
            editingSyllabuses.setLessonId(newLesson.getId());
            editingSyllabuses.setSyllabusStartDate(LocalDateTime.now().plusMinutes(50));
            editingSyllabuses.setSyllabusEndDate(LocalDateTime.now().plusMinutes(60));
        }

        @DisplayName("Edit Syllabus Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:syllabuses"})
        @Test
        void editSyllabusSuccessfully() throws Exception {
            mockMvc.perform(put(SyllabusManagementController.ENDPOINT + "/" + syllabus.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(editingSyllabuses)))
                    .andExpect(status().isPermanentRedirect())
                    .andExpect(redirectedUrl(SyllabusManagementController.ENDPOINT + "/" + syllabus.getId()));

            mockMvc.perform(get(SyllabusManagementController.ENDPOINT + "/" + syllabus.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.syllabusId", is(syllabus.getId().intValue())))
                    .andExpect(jsonPath("$.syllabusNote", is(editingSyllabuses.getSyllabusNote())))
                    .andExpect(jsonPath("$.classroom.classRoomId", is(newClassroom.getId().intValue())))
                    .andExpect(jsonPath("$.classroom.classRoomTag", is(newClassroom.getClassRoomTag())))
                    .andExpect(jsonPath("$.classroom.advisorTeacher.userId", is(teacher.getId().toString())))
                    .andExpect(jsonPath("$.classroom.advisorTeacher.firstName", is(teacher.getFirstName())))
                    .andExpect(jsonPath("$.classroom.advisorTeacher.lastName", is(teacher.getLastName())))
                    .andExpect(jsonPath("$.classroom.grade.gradeId", is(grade.getId().intValue())))
                    .andExpect(jsonPath("$.teacher.userId", is(newTeacher.getId().toString())))
                    .andExpect(jsonPath("$.teacher.firstName", is(newTeacher.getFirstName())))
                    .andExpect(jsonPath("$.teacher.lastName", is(newTeacher.getLastName())))
                    .andExpect(jsonPath("$.lesson.lessonId", is(newLesson.getId().intValue())))
                    .andExpect(jsonPath("$.lesson.lessonName", is(newLesson.getLessonName())))
                    .andExpect(jsonPath("$.organization.organizationId", is(organization.getId().intValue())))
                    .andExpect(jsonPath("$.organization.organizationName", is(organization.getOrganizationName())))
                    .andExpect(jsonPath("$.syllabusStartDate", is(editingSyllabuses.getSyllabusStartDate().format(DateTimeFormatter.ISO_DATE_TIME).toString())))
                    .andExpect(jsonPath("$.syllabusEndDate", is(editingSyllabuses.getSyllabusEndDate().format(DateTimeFormatter.ISO_DATE_TIME).toString())));

        }

        @DisplayName("Update Syllabuses By Classroom Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:syllabuses"})
        @Test
        void updateSyllabusesByClassroomNotFoundError() throws Exception {
            editingSyllabuses.setClassroomId(12312312L);

            mockMvc.perform(put(SyllabusManagementController.ENDPOINT + "/" + syllabus.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingSyllabuses)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc())));
        }

        @DisplayName("Update Syllabuses By Lesson Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:syllabuses"})
        @Test
        void updateSyllabusesByLessonNotFoundError() throws Exception {
            editingSyllabuses.setLessonId(123123123L);

            mockMvc.perform(put(SyllabusManagementController.ENDPOINT + "/" + syllabus.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingSyllabuses)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.LESSON_NOT_FOUND.getDesc())));
        }

        @DisplayName("Update Syllabuses By Teacher Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:syllabuses"})
        @Test
        void updateSyllabusesByTeacherNotFoundError() throws Exception {
            editingSyllabuses.setTeacherId(UUID.randomUUID().toString());

            mockMvc.perform(put(SyllabusManagementController.ENDPOINT + "/" + syllabus.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingSyllabuses)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.TEACHER_NOT_FOUND.getDesc())));
        }

        @DisplayName("Update Syllabuses By Organization Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:syllabuses"})
        @Test
        void updateSyllabusesByOrganizationNotFoundError() throws Exception {
            editingSyllabuses.setOrganizationId(12312312L);

            mockMvc.perform(put(SyllabusManagementController.ENDPOINT + "/" + syllabus.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingSyllabuses)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));
        }

        @DisplayName("Update Syllabus Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:syllabuses"})
        @Test
        void updateSyllabusNotFoundError() throws Exception {
            mockMvc.perform(put(SyllabusManagementController.ENDPOINT + "/12312312")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingSyllabuses)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.SYLLABUS_NOT_FOUND.getDesc())));
        }

    }

    @DisplayName("Deleting Syllabus")
    @Nested
    class DeletingSyllabus {

        @DisplayName("Delete Syllabus Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:syllabuses"})
        @Test
        void deleteSyllabusSuccessfully() throws Exception {
            mockMvc.perform(delete(SyllabusManagementController.ENDPOINT + "/" + syllabus.getId()))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @DisplayName("Delete Syllabus Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:syllabuses"})
        @Test
        void deleteSyllabusNotFoundError() throws Exception {
            mockMvc.perform(delete(SyllabusManagementController.ENDPOINT + "/12312312"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.SYLLABUS_NOT_FOUND.getDesc())));
        }

    }


}
