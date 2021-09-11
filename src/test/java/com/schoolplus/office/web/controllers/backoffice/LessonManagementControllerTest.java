package com.schoolplus.office.web.controllers.backoffice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolplus.office.domain.Lesson;
import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.repository.LessonRepository;
import com.schoolplus.office.repository.OrganizationRepository;
import com.schoolplus.office.web.models.CreatingLessonDto;
import com.schoolplus.office.web.models.EditingLessonDto;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LessonManagementControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    LessonRepository lessonRepository;

    Organization organization;
    Lesson lesson;

    @BeforeEach
    void setUp() {
        organization = new Organization();
        organization.setOrganizationName(RandomStringUtils.random(10, true, false));

        organizationRepository.save(organization);

        lesson = new Lesson();
        lesson.setLessonName(RandomStringUtils.random(10, true, false));
        lesson.setOrganization(organization);

        lessonRepository.save(lesson);
    }

    @DisplayName("Creating Lesson")
    @Nested
    class CreatingLesson {

        CreatingLessonDto creatingLesson;

        @BeforeEach
        void setUp() {
            creatingLesson = new CreatingLessonDto();
            creatingLesson.setLessonName(RandomStringUtils.random(10, true, false));
            creatingLesson.setOrganizationId(organization.getId());
        }

        @DisplayName("Create Lesson Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:lessons"})
        @Test
        void createLessonSuccessfully() throws Exception {

            mockMvc.perform(post(LessonManagementController.ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(creatingLesson)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.lessonId").isNotEmpty())
                    .andExpect(jsonPath("$.lessonName", is(creatingLesson.getLessonName())))
                    .andExpect(jsonPath("$.organization.organizationId", is(organization.getId().intValue())))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Create Lesson Organization Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:lessons"})
        @Test
        void createLessonOrganizationNotFoundError() throws Exception {

            creatingLesson.setOrganizationId(12312312L);

            mockMvc.perform(post(LessonManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingLesson)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));

        }

    }

    @DisplayName("Getting Lessons")
    @Nested
    class GettingLessons {

        @DisplayName("Get Lessons Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:lessons"})
        @Test
        void getLessonsSuccessfully() throws Exception {

            mockMvc.perform(get(LessonManagementController.ENDPOINT + "?size=100"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..lessonId").isNotEmpty())
                    .andExpect(jsonPath("$..lessonName", anyOf(hasItem(is(lesson.getLessonName())))))
                    .andExpect(jsonPath("$..organization.organizationId", anyOf(hasItem(is(organization.getId().intValue())))))
                    .andExpect(jsonPath("$..createdAt").isNotEmpty())
                    .andExpect(jsonPath("$..lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Get Lessons By Organization Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:lessons"})
        @Test
        void getLessonsByOrganizationSuccessfully() throws Exception {

            mockMvc.perform(get(LessonManagementController.ENDPOINT + "/organization/" + organization.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..lessonId").isNotEmpty())
                    .andExpect(jsonPath("$..lessonName", anyOf(hasItem(is(lesson.getLessonName())))))
                    .andExpect(jsonPath("$..organization.organizationId", anyOf(hasItem(is(organization.getId().intValue())))))
                    .andExpect(jsonPath("$..createdAt").isNotEmpty())
                    .andExpect(jsonPath("$..lastModifiedAt").isNotEmpty());

        }


        @DisplayName("Get Lesson Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:lessons"})
        @Test
        void getLessonSuccessfully() throws Exception {

            mockMvc.perform(get(LessonManagementController.ENDPOINT + "/" + lesson.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.lessonId").isNotEmpty())
                    .andExpect(jsonPath("$.lessonName", is(lesson.getLessonName())))
                    .andExpect(jsonPath("$.organization.organizationId", is(organization.getId().intValue())))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Get Lesson Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:lessons"})
        @Test
        void getLessonNotFoundError() throws Exception {

            mockMvc.perform(get(LessonManagementController.ENDPOINT + "/123123213"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.LESSON_NOT_FOUND.getDesc())));

        }
    }

    @DisplayName("Editing Lesson")
    @Nested
    class EditingLesson {

        EditingLessonDto editingLesson;

        @BeforeEach
        void setUp() {
            editingLesson = new EditingLessonDto();
            editingLesson.setLessonName(RandomStringUtils.random(10, true, false));
        }

        @DisplayName("Edit Lesson Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:lessons"})
        @Test
        void editLessonSuccessfully() throws Exception {

            mockMvc.perform(put(LessonManagementController.ENDPOINT + "/" + lesson.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(editingLesson)))
                    .andExpect(status().isPermanentRedirect())
                    .andExpect(redirectedUrl(LessonManagementController.ENDPOINT + "/" + lesson.getId()));

            mockMvc.perform(get(LessonManagementController.ENDPOINT + "/" + lesson.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.lessonId", is(lesson.getId().intValue())))
                    .andExpect(jsonPath("$.lessonName", is(editingLesson.getLessonName())))
                    .andExpect(jsonPath("$.organization.organizationId", is(organization.getId().intValue())))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());


        }

        @DisplayName("Edit Lesson Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:lessons"})
        @Test
        void editLessonNotFoundError() throws Exception {

            mockMvc.perform(put(LessonManagementController.ENDPOINT + "/123123123")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(editingLesson)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.LESSON_NOT_FOUND.getDesc())));

        }

        @DisplayName("Edit Lesson Organization Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:lessons"})
        @Test
        void editLessonOrganizationNotFoundError() throws Exception {

            editingLesson.setOrganizationId(123123123L);

            mockMvc.perform(put(LessonManagementController.ENDPOINT + "/" + lesson.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingLesson)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));

        }

    }

    @DisplayName("Deleting Lesson")
    @Nested
    class DeletingLessons {

        @DisplayName("Delete Lesson Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:lessons"})
        @Test
        void deleteLessonSuccessfully() throws Exception {

            mockMvc.perform(delete(LessonManagementController.ENDPOINT + "/" + lesson.getId()))
                    .andDo(print())
                    .andExpect(status().isNoContent());

        }

        @DisplayName("Delete Lesson Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:lessons"})
        @Test
        void deleteLessonNotFoundError() throws Exception {

            mockMvc.perform(delete(LessonManagementController.ENDPOINT + "/12312312"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.LESSON_NOT_FOUND.getDesc())));

        }
    }

}
