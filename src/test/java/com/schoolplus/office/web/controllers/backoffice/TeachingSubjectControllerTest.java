package com.schoolplus.office.web.controllers.backoffice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.domain.Teacher;
import com.schoolplus.office.domain.TeachingSubject;
import com.schoolplus.office.repository.OrganizationRepository;
import com.schoolplus.office.repository.TeachingSubjectRepository;
import com.schoolplus.office.repository.UserRepository;
import com.schoolplus.office.web.models.CreatingTeachingSubjectDto;
import com.schoolplus.office.web.models.EditingTeachingSubjectDto;
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

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TeachingSubjectControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TeachingSubjectRepository teachingSubjectRepository;

    CreatingTeachingSubjectDto creatingTeachingSubject;

    Organization organization;
    Teacher teacher;

    @BeforeEach
    void setUp() {
        organization = new Organization();
        organization.setOrganizationName(RandomStringUtils.random(10, true, false));

        organizationRepository.save(organization);

        teacher = new Teacher();
        teacher.setUsername(RandomStringUtils.random(10, true, false));
        teacher.setOrganization(organization);

        userRepository.save(teacher);

        creatingTeachingSubject = new CreatingTeachingSubjectDto();
        creatingTeachingSubject.setSubjectName(RandomStringUtils.random(10, true, false));
        creatingTeachingSubject.setTeachers(List.of(teacher.getId().toString()));
        creatingTeachingSubject.setOrganizationId(organization.getId());
    }

    @DisplayName("Creating Teaching Subject")
    @Nested
    class CreatingTeachingSubject {

        @DisplayName("Create Teaching Subject Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:teachingsubjects"})
        @Test
        void createTeachingSubjectSuccessfully() throws Exception {

            mockMvc.perform(post(TeachingSubjectController.ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(creatingTeachingSubject)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.teachingSubjectId").isNotEmpty())
                    .andExpect(jsonPath("$.subjectName", is(creatingTeachingSubject.getSubjectName())))
                    .andExpect(jsonPath("$.teachers[*].userId", anyOf(hasItem(is(teacher.getId().toString())))))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Create Teaching Subject Teacher Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:teachingsubjects"})
        @Test
        void createTeachingSubjectTeacherNotFoundError() throws Exception {

            creatingTeachingSubject.setTeachers(List.of(UUID.randomUUID().toString()));

            mockMvc.perform(post(TeachingSubjectController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingTeachingSubject)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.TEACHER_NOT_FOUND.getDesc())));

        }

        @DisplayName("Create Teaching Subject Organization Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:teachingsubjects"})
        @Test
        void createTeachingSubjectOrganizationNotFoundError() throws Exception {

            creatingTeachingSubject.setOrganizationId(123123123L);

            mockMvc.perform(post(TeachingSubjectController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingTeachingSubject)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));

        }

    }

    @DisplayName("Getting Teaching Subject")
    @Nested
    class GettingTeachingSubject {

        Teacher newTeacher;
        TeachingSubject teachingSubject;
        Organization newOrganization;

        @BeforeEach
        void setUp() {
            newOrganization = new Organization();
            newOrganization.setOrganizationName(RandomStringUtils.random(10, true, false));

            organizationRepository.save(newOrganization);

            teachingSubject = new TeachingSubject();
            teachingSubject.setSubjectName(RandomStringUtils.random(10, true, false));
            teachingSubject.setOrganization(organization);

            teachingSubjectRepository.save(teachingSubject);

            newTeacher = new Teacher();
            newTeacher.setUsername(RandomStringUtils.random(10, true, false));
            newTeacher.setOrganization(organization);
            newTeacher.addTeachingSubject(teachingSubject);

            userRepository.save(newTeacher);

        }

        @DisplayName("Get Teaching Subject Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:teachingsubjects"})
        @Test
        void getTeachingSubjectSuccessfully() throws Exception {
            mockMvc.perform(get(TeachingSubjectController.ENDPOINT + "/" + teachingSubject.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.teachingSubjectId", is(teachingSubject.getId().intValue())))
                    .andExpect(jsonPath("$.subjectName", is(teachingSubject.getSubjectName())))
                    .andExpect(jsonPath("$.organization.organizationName", is(organization.getOrganizationName())))
                    .andExpect(jsonPath("$.teachers[*].userId", anyOf(hasItem(is(newTeacher.getId().toString())))))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());
        }

        @DisplayName("Get Teaching Subjects Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:teachingsubjects"})
        @Test
        void getTeachingSubjectsSuccessfully() throws Exception {
            mockMvc.perform(get(TeachingSubjectController.ENDPOINT + "?page=0&size=10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..teachingSubjectId").isNotEmpty())
                    .andExpect(jsonPath("$..subjectName").isNotEmpty())
                    .andExpect(jsonPath("$..organization.organizationName").isNotEmpty())
                    .andExpect(jsonPath("$..teachers[*].userId").isNotEmpty())
                    .andExpect(jsonPath("$..createdAt").isNotEmpty())
                    .andExpect(jsonPath("$..lastModifiedAt").isNotEmpty());
        }

        @DisplayName("Get Teaching Subject Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:teachingsubjects"})
        @Test
        void getTeachingSubjectNotFoundError() throws Exception {
            mockMvc.perform(get(TeachingSubjectController.ENDPOINT + "/12312312"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.TEACHING_SUBJECT_NOT_FOUND.getDesc())));
        }

    }

    @DisplayName("Editing Teaching Subject")
    @Nested
    class EditingTeachingSubject {

        EditingTeachingSubjectDto editingTeachingSubject;
        Teacher newTeacher;
        TeachingSubject teachingSubject;
        Organization newOrganization;

        @BeforeEach
        void setUp() {
            newOrganization = new Organization();
            newOrganization.setOrganizationName(RandomStringUtils.random(10, true, false));

            organizationRepository.save(newOrganization);

            teachingSubject = new TeachingSubject();
            teachingSubject.setSubjectName(RandomStringUtils.random(10, true, false));
            teachingSubject.setOrganization(organization);

            teachingSubjectRepository.save(teachingSubject);

            newTeacher = new Teacher();
            newTeacher.setUsername(RandomStringUtils.random(10, true, false));
            newTeacher.setOrganization(organization);
            newTeacher.addTeachingSubject(teachingSubject);

            userRepository.save(newTeacher);

            editingTeachingSubject = new EditingTeachingSubjectDto();
            editingTeachingSubject.setSubjectName(RandomStringUtils.random(10, true, false));
            editingTeachingSubject.setOrganizationId(newOrganization.getId());
            editingTeachingSubject.setAddedTeachers(List.of(newTeacher.getId().toString()));
            editingTeachingSubject.setRemovedTeachers(List.of(teacher.getId().toString()));
        }

        @DisplayName("Update Teaching Subject Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:teachingsubjects"})
        @Test
        void updateTeachingSubjectSuccessfully() throws Exception {
            mockMvc.perform(put(TeachingSubjectController.ENDPOINT + "/" + teachingSubject.getId().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(editingTeachingSubject)))
                    .andExpect(status().isPermanentRedirect())
                    .andExpect(redirectedUrl(TeachingSubjectController.ENDPOINT + "/" + teachingSubject.getId().toString()));

            mockMvc.perform(get(TeachingSubjectController.ENDPOINT + "/" + teachingSubject.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.teachingSubjectId", is(teachingSubject.getId().intValue())))
                    .andExpect(jsonPath("$.subjectName", is(editingTeachingSubject.getSubjectName())))
                    .andExpect(jsonPath("$.organization.organizationName", is(newOrganization.getOrganizationName())))
                    .andExpect(jsonPath("$.teachers[*].userId", anyOf(hasItem(is(newTeacher.getId().toString())))))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());
        }

        @DisplayName("Update Teaching Subject Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:teachingsubjects"})
        @Test
        void updateTeachingSubjectNotFoundError() throws Exception {
            mockMvc.perform(put(TeachingSubjectController.ENDPOINT + "/12312312")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingTeachingSubject)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.TEACHING_SUBJECT_NOT_FOUND.getDesc())));
        }

        @DisplayName("Edit Teaching Subject Teacher Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:teachingsubjects"})
        @Test
        void editTeachingSubjectTeacherNotFoundError() throws Exception {

            editingTeachingSubject.setAddedTeachers(List.of(UUID.randomUUID().toString()));

            mockMvc.perform(put(TeachingSubjectController.ENDPOINT + "/" + teachingSubject.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingTeachingSubject)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.TEACHER_NOT_FOUND.getDesc())));

        }

        @DisplayName("Edit Teaching Subject Organization Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:teachingsubjects"})
        @Test
        void editTeachingSubjectOrganizationNotFoundError() throws Exception {

            editingTeachingSubject.setOrganizationId(123123123L);

            mockMvc.perform(put(TeachingSubjectController.ENDPOINT + "/" + teachingSubject.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingTeachingSubject)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));

        }


    }

    @DisplayName("Deleting Teaching Subject")
    @Nested
    class DeletingTeachingSubject {

        Teacher newTeacher;
        TeachingSubject teachingSubject;
        Organization newOrganization;

        @BeforeEach
        void setUp() {
            newOrganization = new Organization();
            newOrganization.setOrganizationName(RandomStringUtils.random(10, true, false));

            organizationRepository.save(newOrganization);

            teachingSubject = new TeachingSubject();
            teachingSubject.setSubjectName(RandomStringUtils.random(10, true, false));
            teachingSubject.setOrganization(organization);

            teachingSubjectRepository.save(teachingSubject);

            newTeacher = new Teacher();
            newTeacher.setUsername(RandomStringUtils.random(10, true, false));
            newTeacher.setOrganization(organization);
            newTeacher.addTeachingSubject(teachingSubject);

            userRepository.save(newTeacher);
        }

        @DisplayName("Delete Teaching Subject Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:teachingsubjects"})
        @Test
        void deleteTeachingSubjectSuccessfully() throws Exception {
            mockMvc.perform(delete(TeachingSubjectController.ENDPOINT + "/" + teachingSubject.getId().toString()))
                    .andExpect(status().isNoContent());
        }

        @DisplayName("Delete Teaching Subject Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:teachingsubjects"})
        @Test
        void deleteTeachingSubjectNotFoundError() throws Exception {
            mockMvc.perform(delete(TeachingSubjectController.ENDPOINT + "/12312312"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.TEACHING_SUBJECT_NOT_FOUND.getDesc())));
        }

    }

}
