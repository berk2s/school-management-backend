package com.schoolplus.office.web.controllers.backoffice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolplus.office.domain.*;
import com.schoolplus.office.repository.LessonRepository;
import com.schoolplus.office.repository.OrganizationRepository;
import com.schoolplus.office.repository.PersonalHomeworkRepository;
import com.schoolplus.office.repository.UserRepository;
import com.schoolplus.office.web.models.CreatingPersonalHomeworkDto;
import com.schoolplus.office.web.models.EditingPersonalHomeworkDto;
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
public class PersonalHomeworkManagementControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LessonRepository lessonRepository;

    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    PersonalHomeworkRepository personalHomeworkRepository;

    Student student;
    Teacher teacher;
    Lesson lesson;
    Organization organization;
    PersonalHomework personalHomework;

    @BeforeEach
    void setUp() {
        organization = new Organization();
        organization.setOrganizationName(RandomStringUtils.random(10, true, false));

        organizationRepository.save(organization);

        student = new Student();
        student.setFirstName(RandomStringUtils.random(10, true, false));
        student.setLastName(RandomStringUtils.random(10, true, false));
        student.setOrganization(organization);

        userRepository.save(student);

        teacher = new Teacher();
        teacher.setFirstName(RandomStringUtils.random(10, true, false));
        teacher.setLastName(RandomStringUtils.random(10, true, false));
        teacher.setOrganization(organization);

        userRepository.save(teacher);

        lesson = new Lesson();
        lesson.setLessonName(RandomStringUtils.random(10, true, false));
        lesson.setOrganization(organization);

        lessonRepository.save(lesson);

        personalHomework = new PersonalHomework();
        personalHomework.setPersonalHomeworkName(RandomStringUtils.random(10, true, false));
        personalHomework.setPersonalHomeworkDescription(RandomStringUtils.random(10, true, false));
        personalHomework.setTeacher(teacher);
        personalHomework.setStudent(student);
        personalHomework.setLesson(lesson);
        personalHomework.setDueDate(LocalDateTime.now().plusDays(11));

        personalHomeworkRepository.save(personalHomework);
    }

    @DisplayName("Creating Personal Homework")
    @Nested
    class CreatingPersonalHomework {

        CreatingPersonalHomeworkDto creatingPersonalHomework;

        @BeforeEach
        void setUp() {
            creatingPersonalHomework = new CreatingPersonalHomeworkDto();
            creatingPersonalHomework.setPersonalHomeworkName(RandomStringUtils.random(10, true, false));
            creatingPersonalHomework.setPersonalHomeworkDescription(RandomStringUtils.random(100, true, false));
            creatingPersonalHomework.setLessonId(lesson.getId());
            creatingPersonalHomework.setStudentId(student.getId().toString());
            creatingPersonalHomework.setTeacherId(teacher.getId().toString());
            creatingPersonalHomework.setDueDate(LocalDateTime.now().plusDays(1));
        }

        @DisplayName("Create Personal Homework Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:personalhomeworks"})
        @Test
        void createPersonalHomeworkSuccessfully() throws Exception {
            mockMvc.perform(post(PersonalHomeworkManagementController.ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(creatingPersonalHomework)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.personalHomeworkId").isNotEmpty())
                    .andExpect(jsonPath("$.personalHomeworkName", is(creatingPersonalHomework.getPersonalHomeworkName())))
                    .andExpect(jsonPath("$.personalHomeworkDescription", is(creatingPersonalHomework.getPersonalHomeworkDescription())))
                    .andExpect(jsonPath("$.dueDate", is(creatingPersonalHomework.getDueDate().format(DateTimeFormatter.ISO_DATE_TIME))))
                    .andExpect(jsonPath("$.student.userId", is(student.getId().toString())))
                    .andExpect(jsonPath("$.student.firstName", is(student.getFirstName())))
                    .andExpect(jsonPath("$.student.lastName", is(student.getLastName())))
                    .andExpect(jsonPath("$.teacher.userId", is(teacher.getId().toString())))
                    .andExpect(jsonPath("$.teacher.firstName", is(teacher.getFirstName())))
                    .andExpect(jsonPath("$.teacher.lastName", is(teacher.getLastName())))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());
        }

        @DisplayName("Create Personal Homework Student Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:personalhomeworks"})
        @Test
        void createPersonalHomeworkStudentNotFoundError() throws Exception {

            creatingPersonalHomework.setStudentId(UUID.randomUUID().toString());

            mockMvc.perform(post(PersonalHomeworkManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingPersonalHomework)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.STUDENT_NOT_FOUND.getDesc())));

        }

        @DisplayName("Create Personal Homework Teacher Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:personalhomeworks"})
        @Test
        void createPersonalHomeworkTeacherNotFoundError() throws Exception {

            creatingPersonalHomework.setTeacherId(UUID.randomUUID().toString());

            mockMvc.perform(post(PersonalHomeworkManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingPersonalHomework)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.TEACHER_NOT_FOUND.getDesc())));

        }

        @DisplayName("Create Personal Homework Lesson Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:personalhomeworks"})
        @Test
        void createPersonalHomeworkLessonNotFoundError() throws Exception {

            creatingPersonalHomework.setLessonId(123123L);

            mockMvc.perform(post(PersonalHomeworkManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingPersonalHomework)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.LESSON_NOT_FOUND.getDesc())));

        }

    }

    @DisplayName("Getting Personal Homework")
    @Nested
    class GettingPersonalHomework {

        @DisplayName("Get Personal Homework Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:personalhomeworks"})
        @Test
        void getPersonalHomeworkSuccessfully() throws Exception {

            mockMvc.perform(get(PersonalHomeworkManagementController.ENDPOINT + "/" + personalHomework.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.personalHomeworkId").isNotEmpty())
                    .andExpect(jsonPath("$.personalHomeworkName", is(personalHomework.getPersonalHomeworkName())))
                    .andExpect(jsonPath("$.personalHomeworkDescription", is(personalHomework.getPersonalHomeworkDescription())))
                    .andExpect(jsonPath("$.dueDate", is(personalHomework.getDueDate().format(DateTimeFormatter.ISO_DATE_TIME))))
                    .andExpect(jsonPath("$.student.userId", is(student.getId().toString())))
                    .andExpect(jsonPath("$.student.firstName", is(student.getFirstName())))
                    .andExpect(jsonPath("$.student.lastName", is(student.getLastName())))
                    .andExpect(jsonPath("$.teacher.userId", is(teacher.getId().toString())))
                    .andExpect(jsonPath("$.teacher.firstName", is(teacher.getFirstName())))
                    .andExpect(jsonPath("$.teacher.lastName", is(teacher.getLastName())))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Get Personal Homeworks By Student Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:personalhomeworks"})
        @Test
        void getPersonalHomeworksByStudentSuccessfully() throws Exception {

            mockMvc.perform(get(PersonalHomeworkManagementController.ENDPOINT + "/student/" + student.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..personalHomeworkId", anyOf(hasItem(is(personalHomework.getId().intValue())))))
                    .andExpect(jsonPath("$..personalHomeworkName", anyOf(hasItem(is(personalHomework.getPersonalHomeworkName())))))
                    .andExpect(jsonPath("$..personalHomeworkDescription", anyOf(hasItem(is(personalHomework.getPersonalHomeworkDescription())))))
                    .andExpect(jsonPath("$..dueDate", anyOf(hasItem(is(personalHomework.getDueDate().format(DateTimeFormatter.ISO_DATE_TIME))))))
                    .andExpect(jsonPath("$..student.userId", anyOf(hasItem(is(student.getId().toString())))))
                    .andExpect(jsonPath("$..student.firstName", anyOf(hasItem(is(student.getFirstName())))))
                    .andExpect(jsonPath("$..student.lastName", anyOf(hasItem(is(student.getLastName())))))
                    .andExpect(jsonPath("$..teacher.userId", anyOf(hasItem(is(teacher.getId().toString())))))
                    .andExpect(jsonPath("$..teacher.firstName", anyOf(hasItem(is(teacher.getFirstName())))))
                    .andExpect(jsonPath("$..teacher.lastName", anyOf(hasItem(is(teacher.getLastName())))))
                    .andExpect(jsonPath("$..lesson.lessonId", anyOf(hasItem(is(lesson.getId().intValue())))))
                    .andExpect(jsonPath("$..lesson.lessonName", anyOf(hasItem(is(lesson.getLessonName())))))
                    .andExpect(jsonPath("$..createdAt").isNotEmpty())
                    .andExpect(jsonPath("$..lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Get Personal Homeworks By Teacher Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:personalhomeworks"})
        @Test
        void getPersonalHomeworksByTeacherSuccessfully() throws Exception {

            mockMvc.perform(get(PersonalHomeworkManagementController.ENDPOINT + "/teacher/" + teacher.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..personalHomeworkId", anyOf(hasItem(is(personalHomework.getId().intValue())))))
                    .andExpect(jsonPath("$..personalHomeworkName", anyOf(hasItem(is(personalHomework.getPersonalHomeworkName())))))
                    .andExpect(jsonPath("$..personalHomeworkDescription", anyOf(hasItem(is(personalHomework.getPersonalHomeworkDescription())))))
                    .andExpect(jsonPath("$..dueDate", anyOf(hasItem(is(personalHomework.getDueDate().format(DateTimeFormatter.ISO_DATE_TIME))))))
                    .andExpect(jsonPath("$..student.userId", anyOf(hasItem(is(student.getId().toString())))))
                    .andExpect(jsonPath("$..student.firstName", anyOf(hasItem(is(student.getFirstName())))))
                    .andExpect(jsonPath("$..student.lastName", anyOf(hasItem(is(student.getLastName())))))
                    .andExpect(jsonPath("$..teacher.userId", anyOf(hasItem(is(teacher.getId().toString())))))
                    .andExpect(jsonPath("$..teacher.firstName", anyOf(hasItem(is(teacher.getFirstName())))))
                    .andExpect(jsonPath("$..teacher.lastName", anyOf(hasItem(is(teacher.getLastName())))))
                    .andExpect(jsonPath("$..lesson.lessonId", anyOf(hasItem(is(lesson.getId().intValue())))))
                    .andExpect(jsonPath("$..lesson.lessonName", anyOf(hasItem(is(lesson.getLessonName())))))
                    .andExpect(jsonPath("$..createdAt").isNotEmpty())
                    .andExpect(jsonPath("$..lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Get Personal Homeworks By Lesson Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:personalhomeworks"})
        @Test
        void getPersonalHomeworksByLessonSuccessfully() throws Exception {

            mockMvc.perform(get(PersonalHomeworkManagementController.ENDPOINT + "/lesson/" + lesson.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..personalHomeworkId", anyOf(hasItem(is(personalHomework.getId().intValue())))))
                    .andExpect(jsonPath("$..personalHomeworkName", anyOf(hasItem(is(personalHomework.getPersonalHomeworkName())))))
                    .andExpect(jsonPath("$..personalHomeworkDescription", anyOf(hasItem(is(personalHomework.getPersonalHomeworkDescription())))))
                    .andExpect(jsonPath("$..dueDate", anyOf(hasItem(is(personalHomework.getDueDate().format(DateTimeFormatter.ISO_DATE_TIME))))))
                    .andExpect(jsonPath("$..student.userId", anyOf(hasItem(is(student.getId().toString())))))
                    .andExpect(jsonPath("$..student.firstName", anyOf(hasItem(is(student.getFirstName())))))
                    .andExpect(jsonPath("$..student.lastName", anyOf(hasItem(is(student.getLastName())))))
                    .andExpect(jsonPath("$..teacher.userId", anyOf(hasItem(is(teacher.getId().toString())))))
                    .andExpect(jsonPath("$..teacher.firstName", anyOf(hasItem(is(teacher.getFirstName())))))
                    .andExpect(jsonPath("$..teacher.lastName", anyOf(hasItem(is(teacher.getLastName())))))
                    .andExpect(jsonPath("$..lesson.lessonId", anyOf(hasItem(is(lesson.getId().intValue())))))
                    .andExpect(jsonPath("$..lesson.lessonName", anyOf(hasItem(is(lesson.getLessonName())))))
                    .andExpect(jsonPath("$..createdAt").isNotEmpty())
                    .andExpect(jsonPath("$..lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Get Personal Homework Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:personalhomeworks"})
        @Test
        void getPersonalHomeworkNotFoundError() throws Exception {

            mockMvc.perform(get(PersonalHomeworkManagementController.ENDPOINT + "/12312312312"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.PERSONAL_HOMEWORK_NOT_FOUND.getDesc())));

        }

        @DisplayName("Get Personal Homework By Student Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:personalhomeworks"})
        @Test
        void getPersonalHomeworkByStudentNotFoundError() throws Exception {

            mockMvc.perform(get(PersonalHomeworkManagementController.ENDPOINT + "/student/" + UUID.randomUUID().toString()))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.STUDENT_NOT_FOUND.getDesc())));

        }

        @DisplayName("Get Personal Homework By Teacher Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:personalhomeworks"})
        @Test
        void getPersonalHomeworkByTeacherNotFoundError() throws Exception {

            mockMvc.perform(get(PersonalHomeworkManagementController.ENDPOINT + "/teacher/" + UUID.randomUUID().toString()))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.TEACHER_NOT_FOUND.getDesc())));

        }

        @DisplayName("Get Personal Homework By Lesson Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:personalhomeworks"})
        @Test
        void getPersonalHomeworkByLessonNotFoundError() throws Exception {

            mockMvc.perform(get(PersonalHomeworkManagementController.ENDPOINT + "/lesson/123123123"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.LESSON_NOT_FOUND.getDesc())));

        }
    }

    @DisplayName("Editing Personal Homework")
    @Nested
    class EditingPersonalHomework {

        Student newStudent;
        Teacher newTeacher;
        Lesson newLesson;

        EditingPersonalHomeworkDto editingPersonalHomework;

        @BeforeEach
        void setUp() {
            newStudent = new Student();
            newStudent.setFirstName(RandomStringUtils.random(10, true, false));
            newStudent.setLastName(RandomStringUtils.random(10, true, false));
            newStudent.setOrganization(organization);

            userRepository.save(newStudent);

            newTeacher = new Teacher();
            newTeacher.setFirstName(RandomStringUtils.random(10, true, false));
            newTeacher.setLastName(RandomStringUtils.random(10, true, false));
            newTeacher.setOrganization(organization);

            userRepository.save(newTeacher);

            newLesson = new Lesson();
            newLesson.setLessonName(RandomStringUtils.random(10, true, false));
            newLesson.setOrganization(organization);

            lessonRepository.save(newLesson);

            editingPersonalHomework = new EditingPersonalHomeworkDto();
            editingPersonalHomework.setPersonalHomeworkName(RandomStringUtils.random(10, true, false));
            editingPersonalHomework.setPersonalHomeworkDescription(RandomStringUtils.random(10, true, false));
            editingPersonalHomework.setDueDate(LocalDateTime.now().plusDays(10));
            editingPersonalHomework.setLessonId(newLesson.getId());
            editingPersonalHomework.setTeacherId(newTeacher.getId().toString());
            editingPersonalHomework.setStudentId(newStudent.getId().toString());

        }

        @DisplayName("Edit Personal Homework Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:personalhomeworks"})
        @Test
        void editPersonalHomeworkSuccessfully() throws Exception {

            mockMvc.perform(put(PersonalHomeworkManagementController.ENDPOINT + "/" + personalHomework.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(editingPersonalHomework)))
                    .andExpect(status().isPermanentRedirect())
                    .andExpect(redirectedUrl(PersonalHomeworkManagementController.ENDPOINT + "/" + personalHomework.getId()));

            mockMvc.perform(get(PersonalHomeworkManagementController.ENDPOINT + "/" + personalHomework.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.personalHomeworkId").isNotEmpty())
                    .andExpect(jsonPath("$.personalHomeworkName", is(editingPersonalHomework.getPersonalHomeworkName())))
                    .andExpect(jsonPath("$.personalHomeworkDescription", is(editingPersonalHomework.getPersonalHomeworkDescription())))
                    .andExpect(jsonPath("$.dueDate", is(editingPersonalHomework.getDueDate().format(DateTimeFormatter.ISO_DATE_TIME))))
                    .andExpect(jsonPath("$.student.userId", is(newStudent.getId().toString())))
                    .andExpect(jsonPath("$.student.firstName", is(newStudent.getFirstName())))
                    .andExpect(jsonPath("$.student.lastName", is(newStudent.getLastName())))
                    .andExpect(jsonPath("$.teacher.userId", is(newTeacher.getId().toString())))
                    .andExpect(jsonPath("$.teacher.firstName", is(newTeacher.getFirstName())))
                    .andExpect(jsonPath("$.teacher.lastName", is(newTeacher.getLastName())))
                    .andExpect(jsonPath("$.lesson.lessonId", is(newLesson.getId().intValue())))
                    .andExpect(jsonPath("$.lesson.lessonName", is(newLesson.getLessonName())))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());


        }

        @DisplayName("Edit Personal Homework Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:personalhomeworks"})
        @Test
        void editPersonalHomeworkNotFoundError() throws Exception {

            mockMvc.perform(put(PersonalHomeworkManagementController.ENDPOINT + "/12312312")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(editingPersonalHomework)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.PERSONAL_HOMEWORK_NOT_FOUND.getDesc())));

        }

        @DisplayName("Edit Personal Homework Student Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:personalhomeworks"})
        @Test
        void editPersonalHomeworkStudentNotFoundError() throws Exception {

            editingPersonalHomework.setStudentId(UUID.randomUUID().toString());

            mockMvc.perform(put(PersonalHomeworkManagementController.ENDPOINT + "/" + personalHomework.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingPersonalHomework)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.STUDENT_NOT_FOUND.getDesc())));

        }

        @DisplayName("Edit Personal Homework Teacher Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:personalhomeworks"})
        @Test
        void editPersonalHomeworkTeacherNotFoundError() throws Exception {

            editingPersonalHomework.setTeacherId(UUID.randomUUID().toString());

            mockMvc.perform(put(PersonalHomeworkManagementController.ENDPOINT + "/" + personalHomework.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingPersonalHomework)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.TEACHER_NOT_FOUND.getDesc())));

        }

        @DisplayName("Edit Personal Homework Lesson Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:personalhomeworks"})
        @Test
        void editPersonalHomeworkLessonNotFoundError() throws Exception {

            editingPersonalHomework.setLessonId(1221312312L);

            mockMvc.perform(put(PersonalHomeworkManagementController.ENDPOINT + "/" + personalHomework.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingPersonalHomework)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.LESSON_NOT_FOUND.getDesc())));

        }

    }

    @DisplayName("Deleting Personal Homework")
    @Nested
    class DeletingPersonalHomework {

        @DisplayName("Delete Personal Homework Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:personalhomeworks"})
        @Test
        void deletePersonalHomeworkSuccessfully() throws Exception {

            mockMvc.perform(delete(PersonalHomeworkManagementController.ENDPOINT + "/" + personalHomework.getId()))
                    .andDo(print())
                    .andExpect(status().isNoContent());

        }

        @DisplayName("Delete Personal Homework Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:personalhomeworks"})
        @Test
        void deletePersonalHomeworkNotFoundError() throws Exception {
            mockMvc.perform(delete(PersonalHomeworkManagementController.ENDPOINT + "/12312312312"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.PERSONAL_HOMEWORK_NOT_FOUND.getDesc())));

        }
    }

}
