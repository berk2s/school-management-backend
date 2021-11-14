package com.schoolplus.office.web.controllers.backoffice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolplus.office.domain.Classroom;
import com.schoolplus.office.domain.Grade;
import com.schoolplus.office.domain.GradeCategory;
import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.repository.ClassroomRepository;
import com.schoolplus.office.repository.GradeCategoryRepository;
import com.schoolplus.office.repository.GradeRepository;
import com.schoolplus.office.repository.OrganizationRepository;
import com.schoolplus.office.web.models.CreatingGradeDto;
import com.schoolplus.office.web.models.EditingGradeDto;
import com.schoolplus.office.web.models.ErrorDesc;
import com.schoolplus.office.web.models.ErrorType;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GradeManagementControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    GradeRepository gradeRepository;

    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    ClassroomRepository classroomRepository;

    @Autowired
    GradeCategoryRepository gradeCategoryRepository;

    Grade grade;
    Organization organization;
    Classroom classroom;
    GradeCategory gradeCategory;

    @BeforeEach
    void setUp() {
        organization = new Organization();
        organization.setOrganizationName(RandomStringUtils.random(10, true, false));

        organizationRepository.save(organization);

        classroom = new Classroom();
        classroom.setClassRoomTag(RandomStringUtils.random(10, true, false));
        classroom.setOrganization(organization);

        gradeCategory = new GradeCategory();
        gradeCategory.setGradeCategoryName(RandomStringUtils.random(10, true, false));
        gradeCategory.setOrganization(organization);

        gradeCategoryRepository.save(gradeCategory);

        grade = new Grade();
        grade.setGradeName(RandomStringUtils.random(10, true, false));
        grade.setOrganization(organization);
        grade.addClassroom(classroom);
        grade.setGradeCategory(gradeCategory);

        gradeRepository.save(grade);

        classroomRepository.save(classroom);
    }

    @DisplayName("Getting Grades")
    @Nested
    class GettingGrades {

        @DisplayName("Get Grade Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:grades"})
        @Test
        void getGradeSuccessfully() throws Exception {

            mockMvc.perform(get(GradeManagementController.ENDPOINT + "/" + grade.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.gradeId", is(grade.getId().intValue())))
                    .andExpect(jsonPath("$.gradeName", is(grade.getGradeName())))
                    .andExpect(jsonPath("$.gradeCategory.gradeCategoryId", is(gradeCategory.getId().intValue())))
                    .andExpect(jsonPath("$.gradeCategory.gradeCategoryName", is(gradeCategory.getGradeCategoryName())))
                    .andExpect(jsonPath("$.classrooms..classRoomId", anyOf(hasItem(is(classroom.getId().intValue())))))
                    .andExpect(jsonPath("$.organization.organizationId", is(organization.getId().intValue())))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Get Grades Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:grades"})
        @Test
        void getGradesSuccessfully() throws Exception {

            mockMvc.perform(get(GradeManagementController.ENDPOINT + "?page=0&size=100"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..gradeId").isNotEmpty())
                    .andExpect(jsonPath("$..gradeName").isNotEmpty())
                    .andExpect(jsonPath("$..gradeCategory.gradeCategoryId").isNotEmpty())
                    .andExpect(jsonPath("$..gradeCategory.gradeCategoryName").isNotEmpty())
                    .andExpect(jsonPath("$..organization.organizationId").isNotEmpty())
                    .andExpect(jsonPath("$..createdAt").isNotEmpty())
                    .andExpect(jsonPath("$..lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Get Grades By Organization Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:grades"})
        @Test
        void getGradesByOrganizationSuccessfully() throws Exception {

            mockMvc.perform(get(GradeManagementController.ENDPOINT + "/organization/" + organization.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content..gradeId", anyOf(hasItem(is(grade.getId().intValue())))))
                    .andExpect(jsonPath("$.content..gradeName", anyOf(hasItem(is(grade.getGradeName())))))
                    .andExpect(jsonPath("$.content..gradeCategory..gradeCategoryId", anyOf(hasItem(is(gradeCategory.getId().intValue())))))
                    .andExpect(jsonPath("$.content..gradeCategory..gradeCategoryName", anyOf(hasItem(is(gradeCategory.getGradeCategoryName())))))
                    .andExpect(jsonPath("$.content..organization").isEmpty())
                    .andExpect(jsonPath("$.content..classrooms.length()", anyOf(hasItem(is(0)))))
                    .andExpect(jsonPath("$.content..createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.content..lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Get Grades By Organization Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:grades"})
        @Test
        void getGradesByOrganizationNotFoundError() throws Exception {

            mockMvc.perform(get(GradeManagementController.ENDPOINT + "/organization/1231231232"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));

        }


        @DisplayName("Get Grade Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:grades"})
        @Test
        void getGradeNotFoundError() throws Exception {

            mockMvc.perform(get(GradeManagementController.ENDPOINT + "/123123123"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.GRADE_NOT_FOUND.getDesc())));

        }

    }

    @DisplayName("Creating Grade")
    @Nested
    class CreatingGrade {

        Classroom newClassroom;
        Organization newOrganization;
        CreatingGradeDto creatingGrade;

        @BeforeEach
        void setUp() {
            newOrganization = new Organization();
            newOrganization.setOrganizationName(RandomStringUtils.random(10, true, false));

            organizationRepository.save(newOrganization);

            newClassroom = new Classroom();
            newClassroom.setClassRoomTag(RandomStringUtils.random(10, true, false));
            newClassroom.setOrganization(organization);

            classroomRepository.save(newClassroom);

            creatingGrade = new CreatingGradeDto();
            creatingGrade.setGradeName(RandomStringUtils.random(10, true, false));
            creatingGrade.setOrganizationId(newOrganization.getId());
            creatingGrade.setClassRooms(List.of(newClassroom.getId()));
            creatingGrade.setGradeCategoryId(gradeCategory.getId());
        }

        @DisplayName("Create Grade Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:grades"})
        @Test
        void createGradeSuccessfully() throws Exception {

            mockMvc.perform(post(GradeManagementController.ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(creatingGrade)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.gradeId").isNotEmpty())
                    .andExpect(jsonPath("$.gradeName", is(creatingGrade.getGradeName())))
                    .andExpect(jsonPath("$.gradeCategory.gradeCategoryId", is(gradeCategory.getId().intValue())))
                    .andExpect(jsonPath("$.gradeCategory.gradeCategoryName", is(gradeCategory.getGradeCategoryName())))
                    .andExpect(jsonPath("$.classrooms..classRoomId", anyOf(hasItem(is(newClassroom.getId().intValue())))))
                    .andExpect(jsonPath("$.organization.organizationId", is(newOrganization.getId().intValue())))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());;

        }

        @DisplayName("Create Grade Organization Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:grades"})
        @Test
        void createGradeOrganizationNotFoundError() throws Exception {

            creatingGrade.setOrganizationId(123123123L);

            mockMvc.perform(post(GradeManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingGrade)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));

        }

        @DisplayName("Create Grade Classroom Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:grades"})
        @Test
        void createGradeClassroomNotFoundError() throws Exception {

            creatingGrade.setClassRooms(List.of(123123L));

            mockMvc.perform(post(GradeManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingGrade)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc())));

        }

    }

    @DisplayName("Editing Grade")
    @Nested
    class EditingGrade {

        Classroom newClassroom;
        Organization newOrganization;
        GradeCategory newGradeCategory;
        EditingGradeDto editingGrade;

        @BeforeEach
        void setUp() {
            newOrganization = new Organization();
            newOrganization.setOrganizationName(RandomStringUtils.random(10, true, false));

            organizationRepository.save(newOrganization);

            newClassroom = new Classroom();
            newClassroom.setClassRoomTag(RandomStringUtils.random(10, true, false));
            newClassroom.setOrganization(organization);

            classroomRepository.save(newClassroom);

            newGradeCategory = new GradeCategory();
            newGradeCategory.setGradeCategoryName(RandomStringUtils.random(10, true, false));
            newGradeCategory.setOrganization(organization);

            gradeCategoryRepository.save(newGradeCategory);

            editingGrade = new EditingGradeDto();
            editingGrade.setGradeName(RandomStringUtils.random(10, true, false));
            editingGrade.setOrganizationId(newOrganization.getId());
            editingGrade.setAddedClassrooms(List.of(newClassroom.getId()));
            editingGrade.setRemovedClassrooms(List.of(classroom.getId()));
            editingGrade.setNewGradeCategoryId(newGradeCategory.getId());
        }

        @DisplayName("Edit Grade Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:grades"})
        @Test
        void editGradeSuccessfully() throws Exception {

            mockMvc.perform(put(GradeManagementController.ENDPOINT + "/" + grade.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingGrade)))
                    .andDo(print())
                    .andExpect(status().isPermanentRedirect())
                    .andExpect(redirectedUrl(GradeManagementController.ENDPOINT + "/" + grade.getId()));

            mockMvc.perform(get(GradeManagementController.ENDPOINT + "/" + grade.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.gradeId", is(grade.getId().intValue())))
                    .andExpect(jsonPath("$.gradeName", is(editingGrade.getGradeName())))
                    .andExpect(jsonPath("$.gradeCategory.gradeCategoryId", is(newGradeCategory.getId().intValue())))
                    .andExpect(jsonPath("$.gradeCategory.gradeCategoryName", is(newGradeCategory.getGradeCategoryName())))
                    .andExpect(jsonPath("$.classrooms.length()", is(1)))
                    .andExpect(jsonPath("$.classrooms..classRoomId", anyOf(hasItem(is(newClassroom.getId().intValue())))))
                    .andExpect(jsonPath("$.organization.organizationId", is(newOrganization.getId().intValue())))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Edit Grade Organization Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:grades"})
        @Test
        void editGradeOrganizationNotFoundError() throws Exception {

            editingGrade.setOrganizationId(123123123L);

            mockMvc.perform(put(GradeManagementController.ENDPOINT + "/" + grade.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingGrade)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));

        }

        @DisplayName("Edit Grade Classroom Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:grades"})
        @Test
        void editGradeClassroomNotFoundError() throws Exception {

            editingGrade.setAddedClassrooms(List.of(123123L));

            mockMvc.perform(put(GradeManagementController.ENDPOINT + "/" + grade.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingGrade)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc())));

        }

        @DisplayName("Edit Grade Category Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:grades"})
        @Test
        void editGradeCategoryNotFoundError() throws Exception {

            editingGrade.setNewGradeCategoryId(RandomUtils.nextLong());

            mockMvc.perform(put(GradeManagementController.ENDPOINT + "/" + grade.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingGrade)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.GRADE_CATEGORY_NOT_FOUND.getDesc())));

        }

    }

    @DisplayName("Deleting Grade")
    @Nested
    class DeletingGrade {

        @DisplayName("Delete Grade Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:grades"})
        @Test
        void deleteGradeSuccessfully() throws Exception {

            mockMvc.perform(delete(GradeManagementController.ENDPOINT + "/" + grade.getId()))
                    .andExpect(status().isNoContent());

        }

        @DisplayName("Delete Grade Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:grades"})
        @Test
        void deleteGradeNotFoundError() throws Exception {

            mockMvc.perform(delete(GradeManagementController.ENDPOINT + "/123123123"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.GRADE_NOT_FOUND.getDesc())));


        }
    }

}
