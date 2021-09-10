package com.schoolplus.office.web.controllers.backoffice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolplus.office.domain.*;
import com.schoolplus.office.repository.*;
import com.schoolplus.office.web.models.CreatingHomeworkDto;
import com.schoolplus.office.web.models.EditingHomeworkDto;
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
public class HomeworkManagementControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    ClassroomRepository classroomRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SyllabusRepository syllabusRepository;

    @Autowired
    LessonRepository lessonRepository;

    @Autowired
    HomeworkRepository homeworkRepository;

    Teacher teacher;
    Classroom classroom;
    Syllabus syllabus;
    Lesson lesson;
    Organization organization;
    Homework homework;

    @BeforeEach
    void setUp() {
        organization = new Organization();
        organization.setOrganizationName(RandomStringUtils.random(10, true, false));

        organizationRepository.save(organization);

        teacher = new Teacher();
        teacher.setOrganization(organization);
        teacher.setFirstName(RandomStringUtils.random(10, true, false));
        teacher.setLastName(RandomStringUtils.random(10, true, false));

        userRepository.save(teacher);

        classroom = new Classroom();
        classroom.setOrganization(organization);
        classroom.setClassRoomTag(RandomStringUtils.random(10, true, false));

        classroomRepository.save(classroom);

        lesson = new Lesson();
        lesson.setLessonName(RandomStringUtils.random(10, true, false));
        lesson.setOrganization(organization);

        lessonRepository.save(lesson);

        syllabus = new Syllabus();
        syllabus.setSyllabusStartDate(LocalDateTime.now().plusMinutes(10));
        syllabus.setSyllabusEndDate(LocalDateTime.now().plusMinutes(60));
        syllabus.setOrganization(organization);
        syllabus.setLesson(lesson);
        syllabus.setClassroom(classroom);
        syllabus.setTeacher(teacher);

        syllabusRepository.save(syllabus);

        homework = new Homework();
        homework.setSyllabus(syllabus);
        homework.setTeacher(teacher);
        homework.setClassroom(classroom);
        homework.setHomeworkDescription(RandomStringUtils.random(10, true, false));
        homework.setDueDate(LocalDateTime.now().plusDays(10));

        homeworkRepository.save(homework);
    }

    @DisplayName("Creating Homework")
    @Nested
    class CreatingHomework {

        CreatingHomeworkDto creatingHomework;

        @BeforeEach
        void setUp() {
            creatingHomework = new CreatingHomeworkDto();
            creatingHomework.setHomeworkDescription(RandomStringUtils.random(100, true, false));
            creatingHomework.setClassroomId(classroom.getId());
            creatingHomework.setTeacherId(teacher.getId().toString());
            creatingHomework.setDueDate(LocalDateTime.now().plusDays(10));
            creatingHomework.setSyllabusId(syllabus.getId());
        }

        @DisplayName("Create Homework Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:homeworks"})
        @Test
        void createHomeworkSuccessfully() throws Exception {
            mockMvc.perform(post(HomeworkManagementController.ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(creatingHomework)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.homeworkId").isNotEmpty())
                    .andExpect(jsonPath("$.homeworkDescription", is(creatingHomework.getHomeworkDescription())))
                    .andExpect(jsonPath("$.syllabus.syllabusId", is(syllabus.getId().intValue())))
                    .andExpect(jsonPath("$.syllabus.lesson.lessonId", is(lesson.getId().intValue())))
                    .andExpect(jsonPath("$.syllabus.lesson.lessonName", is(lesson.getLessonName())))
                    .andExpect(jsonPath("$.syllabus.syllabusStartDate", is(syllabus.getSyllabusStartDate().format(DateTimeFormatter.ISO_DATE_TIME))))
                    .andExpect(jsonPath("$.syllabus.syllabusEndDate", is(syllabus.getSyllabusEndDate().format(DateTimeFormatter.ISO_DATE_TIME))))
                    .andExpect(jsonPath("$.classroom.classRoomId", is(classroom.getId().intValue())))
                    .andExpect(jsonPath("$.classroom.classRoomTag", is(classroom.getClassRoomTag())))
                    .andExpect(jsonPath("$.teacher.userId", is(teacher.getId().toString())))
                    .andExpect(jsonPath("$.teacher.firstName", is(teacher.getFirstName())))
                    .andExpect(jsonPath("$.teacher.lastName", is(teacher.getLastName())))
                    .andExpect(jsonPath("$.dueDate", is(creatingHomework.getDueDate().format(DateTimeFormatter.ISO_DATE_TIME))))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Create Homework Classroom Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:homeworks"})
        @Test
        void createHomeworkClassroomNotFoundError() throws Exception {

            creatingHomework.setClassroomId(112312312L);

            mockMvc.perform(post(HomeworkManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingHomework)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc())));

        }

        @DisplayName("Create Homework Teacher Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:homeworks"})
        @Test
        void createHomeworkTeacherNotFoundError() throws Exception {

            creatingHomework.setTeacherId(UUID.randomUUID().toString());

            mockMvc.perform(post(HomeworkManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingHomework)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.TEACHER_NOT_FOUND.getDesc())));

        }

        @DisplayName("Create Homework Syllabus Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:homeworks"})
        @Test
        void createHomeworkSyllabusNotFoundError() throws Exception {

            creatingHomework.setSyllabusId(123123123L);

            mockMvc.perform(post(HomeworkManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingHomework)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.SYLLABUS_NOT_FOUND.getDesc())));

        }
    }

    @DisplayName("Getting Homeworks")
    @Nested
    class GettingHomeworks {

        @DisplayName("Get Homeworks Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:homeworks"})
        @Test
        void getHomeworksSuccessfully() throws Exception {

            mockMvc.perform(get(HomeworkManagementController.ENDPOINT + "?size=100"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..homeworkId", anyOf(hasItem(is(homework.getId().intValue())))))
                    .andExpect(jsonPath("$..homeworkDescription", anyOf(hasItem(is(homework.getHomeworkDescription())))))
                    .andExpect(jsonPath("$..syllabus.syllabusId", anyOf(hasItem(is(syllabus.getId().intValue())))))
                    .andExpect(jsonPath("$..syllabus.lesson.lessonId", anyOf(hasItem(is(lesson.getId().intValue())))))
                    .andExpect(jsonPath("$..syllabus.lesson.lessonName", anyOf(hasItem(is(lesson.getLessonName())))))
                    .andExpect(jsonPath("$..syllabus.syllabusStartDate", anyOf(hasItem(is(syllabus.getSyllabusStartDate().format(DateTimeFormatter.ISO_DATE_TIME))))))
                    .andExpect(jsonPath("$..syllabus.syllabusEndDate", anyOf(hasItem(is(syllabus.getSyllabusEndDate().format(DateTimeFormatter.ISO_DATE_TIME))))))
                    .andExpect(jsonPath("$..classroom.classRoomId", anyOf(hasItem(is(classroom.getId().intValue())))))
                    .andExpect(jsonPath("$..classroom.classRoomTag", anyOf(hasItem(is(classroom.getClassRoomTag())))))
                    .andExpect(jsonPath("$..teacher.userId", anyOf(hasItem(is(teacher.getId().toString())))))
                    .andExpect(jsonPath("$..teacher.firstName", anyOf(hasItem(is(teacher.getFirstName())))))
                    .andExpect(jsonPath("$..teacher.lastName", anyOf(hasItem(is(teacher.getLastName())))))
                    .andExpect(jsonPath("$..dueDate", anyOf(hasItem(is(homework.getDueDate().format(DateTimeFormatter.ISO_DATE_TIME))))))
                    .andExpect(jsonPath("$..createdAt").isNotEmpty())
                    .andExpect(jsonPath("$..lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Get Homework Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:homeworks"})
        @Test
        void getHomeworkSuccessfully() throws Exception {

            mockMvc.perform(get(HomeworkManagementController.ENDPOINT + "/" + homework.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.homeworkId", is(homework.getId().intValue())))
                    .andExpect(jsonPath("$.homeworkDescription", is(homework.getHomeworkDescription())))
                    .andExpect(jsonPath("$.syllabus.syllabusId", is(syllabus.getId().intValue())))
                    .andExpect(jsonPath("$.syllabus.lesson.lessonId", is(lesson.getId().intValue())))
                    .andExpect(jsonPath("$.syllabus.lesson.lessonName", is(lesson.getLessonName())))
                    .andExpect(jsonPath("$.syllabus.syllabusStartDate", is(syllabus.getSyllabusStartDate().format(DateTimeFormatter.ISO_DATE_TIME))))
                    .andExpect(jsonPath("$.syllabus.syllabusEndDate", is(syllabus.getSyllabusEndDate().format(DateTimeFormatter.ISO_DATE_TIME))))
                    .andExpect(jsonPath("$.classroom.classRoomId", is(classroom.getId().intValue())))
                    .andExpect(jsonPath("$.classroom.classRoomTag", is(classroom.getClassRoomTag())))
                    .andExpect(jsonPath("$.teacher.userId", is(teacher.getId().toString())))
                    .andExpect(jsonPath("$.teacher.firstName", is(teacher.getFirstName())))
                    .andExpect(jsonPath("$.teacher.lastName", is(teacher.getLastName())))
                    .andExpect(jsonPath("$.dueDate", is(homework.getDueDate().format(DateTimeFormatter.ISO_DATE_TIME))))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Get Homeworks By Classroom Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:homeworks"})
        @Test
        void getHomeworksByClassroomSuccessfully() throws Exception {

            mockMvc.perform(get(HomeworkManagementController.ENDPOINT + "/classroom/" + classroom.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..homeworkId", anyOf(hasItem(is(homework.getId().intValue())))))
                    .andExpect(jsonPath("$..homeworkDescription", anyOf(hasItem(is(homework.getHomeworkDescription())))))
                    .andExpect(jsonPath("$..syllabus.syllabusId", anyOf(hasItem(is(syllabus.getId().intValue())))))
                    .andExpect(jsonPath("$..syllabus.lesson.lessonId", anyOf(hasItem(is(lesson.getId().intValue())))))
                    .andExpect(jsonPath("$..syllabus.lesson.lessonName", anyOf(hasItem(is(lesson.getLessonName())))))
                    .andExpect(jsonPath("$..syllabus.syllabusStartDate", anyOf(hasItem(is(syllabus.getSyllabusStartDate().format(DateTimeFormatter.ISO_DATE_TIME))))))
                    .andExpect(jsonPath("$..syllabus.syllabusEndDate", anyOf(hasItem(is(syllabus.getSyllabusEndDate().format(DateTimeFormatter.ISO_DATE_TIME))))))
                    .andExpect(jsonPath("$..classroom.classRoomId", anyOf(hasItem(is(classroom.getId().intValue())))))
                    .andExpect(jsonPath("$..classroom.classRoomTag", anyOf(hasItem(is(classroom.getClassRoomTag())))))
                    .andExpect(jsonPath("$..teacher.userId", anyOf(hasItem(is(teacher.getId().toString())))))
                    .andExpect(jsonPath("$..teacher.firstName", anyOf(hasItem(is(teacher.getFirstName())))))
                    .andExpect(jsonPath("$..teacher.lastName", anyOf(hasItem(is(teacher.getLastName())))))
                    .andExpect(jsonPath("$..dueDate", anyOf(hasItem(is(homework.getDueDate().format(DateTimeFormatter.ISO_DATE_TIME))))))
                    .andExpect(jsonPath("$..createdAt").isNotEmpty())
                    .andExpect(jsonPath("$..lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Get Homeworks By Teacher Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:homeworks"})
        @Test
        void getHomeworksByTeacherSuccessfully() throws Exception {

            mockMvc.perform(get(HomeworkManagementController.ENDPOINT + "/teacher/" + teacher.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..homeworkId", anyOf(hasItem(is(homework.getId().intValue())))))
                    .andExpect(jsonPath("$..homeworkDescription", anyOf(hasItem(is(homework.getHomeworkDescription())))))
                    .andExpect(jsonPath("$..syllabus.syllabusId", anyOf(hasItem(is(syllabus.getId().intValue())))))
                    .andExpect(jsonPath("$..syllabus.lesson.lessonId", anyOf(hasItem(is(lesson.getId().intValue())))))
                    .andExpect(jsonPath("$..syllabus.lesson.lessonName", anyOf(hasItem(is(lesson.getLessonName())))))
                    .andExpect(jsonPath("$..syllabus.syllabusStartDate", anyOf(hasItem(is(syllabus.getSyllabusStartDate().format(DateTimeFormatter.ISO_DATE_TIME))))))
                    .andExpect(jsonPath("$..syllabus.syllabusEndDate", anyOf(hasItem(is(syllabus.getSyllabusEndDate().format(DateTimeFormatter.ISO_DATE_TIME))))))
                    .andExpect(jsonPath("$..classroom.classRoomId", anyOf(hasItem(is(classroom.getId().intValue())))))
                    .andExpect(jsonPath("$..classroom.classRoomTag", anyOf(hasItem(is(classroom.getClassRoomTag())))))
                    .andExpect(jsonPath("$..teacher.userId", anyOf(hasItem(is(teacher.getId().toString())))))
                    .andExpect(jsonPath("$..teacher.firstName", anyOf(hasItem(is(teacher.getFirstName())))))
                    .andExpect(jsonPath("$..teacher.lastName", anyOf(hasItem(is(teacher.getLastName())))))
                    .andExpect(jsonPath("$..dueDate", anyOf(hasItem(is(homework.getDueDate().format(DateTimeFormatter.ISO_DATE_TIME))))))
                    .andExpect(jsonPath("$..createdAt").isNotEmpty())
                    .andExpect(jsonPath("$..lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Get Homeworks By Syllabus Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:homeworks"})
        @Test
        void getHomeworksBySyllabusSuccessfully() throws Exception {

            mockMvc.perform(get(HomeworkManagementController.ENDPOINT + "/syllabus/" + syllabus.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..homeworkId", anyOf(hasItem(is(homework.getId().intValue())))))
                    .andExpect(jsonPath("$..homeworkDescription", anyOf(hasItem(is(homework.getHomeworkDescription())))))
                    .andExpect(jsonPath("$..syllabus.syllabusId", anyOf(hasItem(is(syllabus.getId().intValue())))))
                    .andExpect(jsonPath("$..syllabus.lesson.lessonId", anyOf(hasItem(is(lesson.getId().intValue())))))
                    .andExpect(jsonPath("$..syllabus.lesson.lessonName", anyOf(hasItem(is(lesson.getLessonName())))))
                    .andExpect(jsonPath("$..syllabus.syllabusStartDate", anyOf(hasItem(is(syllabus.getSyllabusStartDate().format(DateTimeFormatter.ISO_DATE_TIME))))))
                    .andExpect(jsonPath("$..syllabus.syllabusEndDate", anyOf(hasItem(is(syllabus.getSyllabusEndDate().format(DateTimeFormatter.ISO_DATE_TIME))))))
                    .andExpect(jsonPath("$..classroom.classRoomId", anyOf(hasItem(is(classroom.getId().intValue())))))
                    .andExpect(jsonPath("$..classroom.classRoomTag", anyOf(hasItem(is(classroom.getClassRoomTag())))))
                    .andExpect(jsonPath("$..teacher.userId", anyOf(hasItem(is(teacher.getId().toString())))))
                    .andExpect(jsonPath("$..teacher.firstName", anyOf(hasItem(is(teacher.getFirstName())))))
                    .andExpect(jsonPath("$..teacher.lastName", anyOf(hasItem(is(teacher.getLastName())))))
                    .andExpect(jsonPath("$..dueDate", anyOf(hasItem(is(homework.getDueDate().format(DateTimeFormatter.ISO_DATE_TIME))))))
                    .andExpect(jsonPath("$..createdAt").isNotEmpty())
                    .andExpect(jsonPath("$..lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Get Homework Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:homeworks"})
        @Test
        void getHomeworkNotFoundError() throws Exception {

            mockMvc.perform(get(HomeworkManagementController.ENDPOINT + "/123123123"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.HOMEWORK_NOT_FOUND.getDesc())));

        }

        @DisplayName("Get Homeworks By Classroom Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:homeworks"})
        @Test
        void getHomeworksByClassroomNotFoundError() throws Exception {

            mockMvc.perform(get(HomeworkManagementController.ENDPOINT + "/classroom/123123123"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc())));

        }

        @DisplayName("Get Homeworks By Teacher Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:homeworks"})
        @Test
        void getHomeworksByTeacherNotFoundError() throws Exception {

            mockMvc.perform(get(HomeworkManagementController.ENDPOINT + "/teacher/" + UUID.randomUUID().toString()))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.TEACHER_NOT_FOUND.getDesc())));

        }


        @DisplayName("Get Homeworks By Syllabus Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:homeworks"})
        @Test
        void getHomeworksBySyllabusNotFoundError() throws Exception {

            mockMvc.perform(get(HomeworkManagementController.ENDPOINT + "/syllabus/12312312312"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.SYLLABUS_NOT_FOUND.getDesc())));

        }
    }

    @DisplayName("Editing Homework")
    @Nested
    class EditingHomework {

        Teacher newTeacher;
        Classroom newClassroom;
        Syllabus newSyllabus;
        Lesson newLesson;

        EditingHomeworkDto editingHomework;

        @BeforeEach
        void setUp() {
            newTeacher = new Teacher();
            newTeacher.setOrganization(organization);
            newTeacher.setFirstName(RandomStringUtils.random(10, true, false));
            newTeacher.setLastName(RandomStringUtils.random(10, true, false));

            userRepository.save(newTeacher);

            newClassroom = new Classroom();
            newClassroom.setOrganization(organization);
            newClassroom.setClassRoomTag(RandomStringUtils.random(10, true, false));

            classroomRepository.save(newClassroom);

            newLesson = new Lesson();
            newLesson.setLessonName(RandomStringUtils.random(10, true, false));
            newLesson.setOrganization(organization);

            lessonRepository.save(newLesson);

            newSyllabus = new Syllabus();
            newSyllabus.setSyllabusStartDate(LocalDateTime.now().plusMinutes(10));
            newSyllabus.setSyllabusEndDate(LocalDateTime.now().plusMinutes(60));
            newSyllabus.setOrganization(organization);
            newSyllabus.setLesson(newLesson);
            newSyllabus.setClassroom(newClassroom);
            newSyllabus.setTeacher(newTeacher);

            syllabusRepository.save(newSyllabus);

            editingHomework = new EditingHomeworkDto();
            editingHomework.setHomeworkDescription(RandomStringUtils.random(100, true, false));
            editingHomework.setDueDate(LocalDateTime.now().plusDays(100));
            editingHomework.setClassroomId(newClassroom.getId());
            editingHomework.setSyllabusId(newSyllabus.getId());
            editingHomework.setTeacherId(newTeacher.getId().toString());
        }

        @DisplayName("Edit Homework Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:homeworks"})
        @Test
        void editHomeworkSuccessfully() throws Exception {

            mockMvc.perform(put(HomeworkManagementController.ENDPOINT + "/" + homework.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(editingHomework)))
                    .andExpect(status().isPermanentRedirect())
                    .andExpect(redirectedUrl(HomeworkManagementController.ENDPOINT + "/" + homework.getId()));

            mockMvc.perform(get(HomeworkManagementController.ENDPOINT + "/" + homework.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.homeworkId", is(homework.getId().intValue())))
                    .andExpect(jsonPath("$.homeworkDescription", is(editingHomework.getHomeworkDescription())))
                    .andExpect(jsonPath("$.syllabus.syllabusId", is(newSyllabus.getId().intValue())))
                    .andExpect(jsonPath("$.syllabus.lesson.lessonId", is(newLesson.getId().intValue())))
                    .andExpect(jsonPath("$.syllabus.lesson.lessonName", is(newLesson.getLessonName())))
                    .andExpect(jsonPath("$.syllabus.syllabusStartDate", is(newSyllabus.getSyllabusStartDate().format(DateTimeFormatter.ISO_DATE_TIME))))
                    .andExpect(jsonPath("$.syllabus.syllabusEndDate", is(newSyllabus.getSyllabusEndDate().format(DateTimeFormatter.ISO_DATE_TIME))))
                    .andExpect(jsonPath("$.classroom.classRoomId", is(newClassroom.getId().intValue())))
                    .andExpect(jsonPath("$.classroom.classRoomTag", is(newClassroom.getClassRoomTag())))
                    .andExpect(jsonPath("$.teacher.userId", is(newTeacher.getId().toString())))
                    .andExpect(jsonPath("$.teacher.firstName", is(newTeacher.getFirstName())))
                    .andExpect(jsonPath("$.teacher.lastName", is(newTeacher.getLastName())))
                    .andExpect(jsonPath("$.dueDate", is(editingHomework.getDueDate().format(DateTimeFormatter.ISO_DATE_TIME))))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Update Homework Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:homeworks"})
        @Test
        void updateHomeworkNotFoundError() throws Exception {

            mockMvc.perform(put(HomeworkManagementController.ENDPOINT + "/12312")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingHomework)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.HOMEWORK_NOT_FOUND.getDesc())));

        }

        @DisplayName("Update Homework Classroom Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:homeworks"})
        @Test
        void updateHomeworkClassroomNotFoundError() throws Exception {

            editingHomework.setClassroomId(112312312L);

            mockMvc.perform(put(HomeworkManagementController.ENDPOINT + "/" + homework.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingHomework)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc())));

        }

        @DisplayName("Update Homework Teacher Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:homeworks"})
        @Test
        void updateHomeworkTeacherNotFoundError() throws Exception {

            editingHomework.setTeacherId(UUID.randomUUID().toString());

            mockMvc.perform(put(HomeworkManagementController.ENDPOINT + "/" + homework.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingHomework)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.TEACHER_NOT_FOUND.getDesc())));

        }

        @DisplayName("Update Homework Syllabus Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:homeworks"})
        @Test
        void updateHomeworkSyllabusNotFoundError() throws Exception {

            editingHomework.setSyllabusId(123123123L);

            mockMvc.perform(put(HomeworkManagementController.ENDPOINT + "/" + homework.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingHomework)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.SYLLABUS_NOT_FOUND.getDesc())));

        }
    }

    @DisplayName("Deleting Homework")
    @Nested
    class DeletingHomework {

        @DisplayName("Delete Homework Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:homeworks"})
        @Test
        void deleteHomeworkSuccessfully() throws Exception {

            mockMvc.perform(delete(HomeworkManagementController.ENDPOINT + "/" + homework.getId()))
                    .andExpect(status().isNoContent());

        }

        @DisplayName("Delete Homework Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:homeworks"})
        @Test
        void deleteHomeworkNotFoundError() throws Exception {

            mockMvc.perform(delete(HomeworkManagementController.ENDPOINT + "/12312321"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.HOMEWORK_NOT_FOUND.getDesc())));

        }
    }
}
