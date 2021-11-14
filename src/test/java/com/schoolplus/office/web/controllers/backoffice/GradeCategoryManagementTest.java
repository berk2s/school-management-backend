package com.schoolplus.office.web.controllers.backoffice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolplus.office.domain.Grade;
import com.schoolplus.office.domain.GradeCategory;
import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.repository.GradeCategoryRepository;
import com.schoolplus.office.repository.GradeRepository;
import com.schoolplus.office.repository.OrganizationRepository;
import com.schoolplus.office.web.models.CreatingGradeCategoryDto;
import com.schoolplus.office.web.models.ErrorDesc;
import com.schoolplus.office.web.models.ErrorType;
import com.schoolplus.office.web.models.EditingGradeCategoryDto;
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
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GradeCategoryManagementTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    GradeCategoryRepository gradeCategoryRepository;

    @Autowired
    GradeRepository gradeRepository;

    Organization organization;
    GradeCategory gradeCategory;
    Grade grade;

    @BeforeEach
    void setUp() {
        organization = new Organization();
        organization.setOrganizationName(RandomStringUtils.random(10, true, false));

        organizationRepository.save(organization);

        gradeCategory = new GradeCategory();
        gradeCategory.setOrganization(organization);
        gradeCategory.setGradeCategoryName(RandomStringUtils.random(10, true, false));

        gradeCategoryRepository.save(gradeCategory);

        grade = new Grade();
        grade.setGradeName(RandomStringUtils.random(10, true, false));
        grade.setOrganization(organization);
        grade.setGradeCategory(gradeCategory);

        gradeRepository.save(grade);

    }

    @DisplayName("Getting Grade Category")
    @Nested
    class GettingGradeCategory {

        @DisplayName("Get Grade Category By Organization Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:grade:categories"})
        @Test
        void getGradeCategoryByOrganizationSuccessfully() throws Exception {
            mockMvc.perform(get(GradeCategoryManagementController.ENDPOINT + "/organization/" + organization.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content..gradeCategoryId", anyOf(hasItem(is(is(gradeCategory.getId().intValue()))))))
                    .andExpect(jsonPath("$.content..gradeCategoryName", anyOf(hasItem(is(is(gradeCategory.getGradeCategoryName()))))))
                    .andExpect(jsonPath("$.content..grades..gradeId", anyOf(hasItem(is(grade.getId().intValue())))))
                    .andExpect(jsonPath("$.content..grades..gradeName", anyOf(hasItem(is(grade.getGradeName())))))
                    .andExpect(jsonPath("$.content..createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.content..lastModifiedAt").isNotEmpty());
        }

        @DisplayName("Get Grade Category By Organization Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:grade:categories"})
        @Test
        void getGradeCategoryByOrganizationNotFoundError() throws Exception {
            mockMvc.perform(get(GradeCategoryManagementController.ENDPOINT + "/organization/" + RandomUtils.nextLong()))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));

        }


        @DisplayName("Get Grade Category Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:grade:categories"})
        @Test
        void getGradeCategorySuccessfully() throws Exception {
            mockMvc.perform(get(GradeCategoryManagementController.ENDPOINT + "/" + gradeCategory.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.gradeCategoryId", is(gradeCategory.getId().intValue())))
                    .andExpect(jsonPath("$.gradeCategoryName", is(gradeCategory.getGradeCategoryName())))
                    .andExpect(jsonPath("$.grades..gradeId", anyOf(hasItem(is(grade.getId().intValue())))))
                    .andExpect(jsonPath("$.grades..gradeName", anyOf(hasItem(is(grade.getGradeName())))))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());
        }

        @DisplayName("Get Grade Category Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:grade:categories"})
        @Test
        void getGradeCategoryNotFoundError() throws Exception {
            mockMvc.perform(get(GradeCategoryManagementController.ENDPOINT + "/" + RandomUtils.nextLong()))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.GRADE_CATEGORY_NOT_FOUND.getDesc())));
        }

    }

    @DisplayName("Creating Grade Category")
    @Nested
    class CreatingGradeCategory {

        CreatingGradeCategoryDto creatingGradeCategoryDto;

        @BeforeEach
        void setUp() {
            creatingGradeCategoryDto = new CreatingGradeCategoryDto();
            creatingGradeCategoryDto.setGradeCategoryName(RandomStringUtils.random(10, true, false));
            creatingGradeCategoryDto.setOrganizationId(organization.getId());
        }

        @DisplayName("Create Grade Category Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:grade:categories"})
        @Test
        void createGradeCategorySuccessfully() throws Exception {

            mockMvc.perform(post(GradeCategoryManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingGradeCategoryDto)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.gradeCategoryId").isNotEmpty())
                    .andExpect(jsonPath("$.gradeCategoryName", is(creatingGradeCategoryDto.getGradeCategoryName())))
                    .andExpect(jsonPath("$.grades.length()", is(0)))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Create Grade Category Organization Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:grade:categories"})
        @Test
        void createGradeCategoryOrganizationNotFoundError() throws Exception {

            creatingGradeCategoryDto.setOrganizationId(RandomUtils.nextLong());

            mockMvc.perform(post(GradeCategoryManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingGradeCategoryDto)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));

        }

    }

    @DisplayName("Updating Grade Category")
    @Nested
    class UpdatingGradeCategory {

        EditingGradeCategoryDto editingGradeCategoryDto;
        Grade newGrade;

        @BeforeEach
        void setUp() {
            newGrade = new Grade();
            newGrade.setGradeName(RandomStringUtils.random(10, true, false));
            newGrade.setOrganization(organization);

            gradeRepository.save(newGrade);

            editingGradeCategoryDto = new EditingGradeCategoryDto();
            editingGradeCategoryDto.setGradeCategoryName(RandomStringUtils.random(10, true, false));
            editingGradeCategoryDto.getDeletedGrades().add(grade.getId());
            editingGradeCategoryDto.getAddedGrades().add(newGrade.getId());
        }

        @DisplayName("Update Grade Category Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:grade:categories"})
        @Test
        void updateGradeCategorySuccessfully() throws Exception {

            mockMvc.perform(put(GradeCategoryManagementController.ENDPOINT + "/" + gradeCategory.getId())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(editingGradeCategoryDto)))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get(GradeCategoryManagementController.ENDPOINT + "/" + gradeCategory.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.gradeCategoryId", is(gradeCategory.getId().intValue())))
                    .andExpect(jsonPath("$.gradeCategoryName", is(editingGradeCategoryDto.getGradeCategoryName())))
                    .andExpect(jsonPath("$.grades.length()", is(1)))
                    .andExpect(jsonPath("$.grades..gradeId", anyOf(hasItem(is(newGrade.getId().intValue())))))
                    .andExpect(jsonPath("$.grades..gradeName", anyOf(hasItem(is(newGrade.getGradeName())))))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Update Grade Category Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:grade:categories"})
        @Test
        void updateGradeCategoryNotFoundError() throws Exception {

            mockMvc.perform(put(GradeCategoryManagementController.ENDPOINT + "/" + RandomUtils.nextLong())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(editingGradeCategoryDto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.GRADE_CATEGORY_NOT_FOUND.getDesc())));

        }

        @DisplayName("Update Grade Category Not Organization Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:grade:categories"})
        @Test
        void updateGradeCategoryOrganizationNotFoundError() throws Exception {

            editingGradeCategoryDto.setOrganizationId(RandomUtils.nextLong());

            mockMvc.perform(put(GradeCategoryManagementController.ENDPOINT + "/" + gradeCategory.getId())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(editingGradeCategoryDto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));

        }
    }

    @DisplayName("Deleting Grade Category")
    @Nested
    class DeletingGradeCategory {

        @DisplayName("Delete Grade Category Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:grade:categories"})
        @Test
        void deleteGradeCategorySuccessfully() throws Exception {

            mockMvc.perform(delete(GradeCategoryManagementController.ENDPOINT + "/" + gradeCategory.getId()))
                    .andDo(print())
                    .andExpect(status().isNoContent());

        }

        @DisplayName("Delete Grade Category Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:grade:categories"})
        @Test
        void deleteGradeCategoryNotFoundError() throws Exception {

            mockMvc.perform(delete(GradeCategoryManagementController.ENDPOINT + "/" + RandomUtils.nextLong()))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.GRADE_CATEGORY_NOT_FOUND.getDesc())));

        }

    }

}
