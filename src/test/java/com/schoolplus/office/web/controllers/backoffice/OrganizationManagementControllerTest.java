package com.schoolplus.office.web.controllers.backoffice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.repository.OrganizationRepository;
import com.schoolplus.office.web.models.CreatingOrganizationDto;
import com.schoolplus.office.web.models.EditingOrganizationDto;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OrganizationManagementControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    OrganizationRepository organizationRepository;

    @DisplayName("Creating Organization")
    @Nested
    class CreatingOrganization {

        CreatingOrganizationDto creatingOrganization;

        @BeforeEach
        void setUp() {
            creatingOrganization = new CreatingOrganizationDto();
            creatingOrganization.setOrganizationName(RandomStringUtils.random(10, true, false));
        }

        @DisplayName("Create Organization Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:organizations"})
        @Test
        void createOrganizationSuccessfully() throws Exception {
            mockMvc.perform(post(OrganizationManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(creatingOrganization)))
                    .andDo(print())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.organizationId").isNotEmpty())
                    .andExpect(jsonPath("$.organizationName", is(creatingOrganization.getOrganizationName())))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());
        }

    }

    @DisplayName("Getting Organizations")
    @Nested
    class GettingOrganizations {

        Organization organization;

        @BeforeEach
        void setUp() {
            organization = new Organization();
            organization.setOrganizationName(RandomStringUtils.random(10, true, false));

            organizationRepository.save(organization);
        }

        @DisplayName("Get Organization Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:organizations"})
        @Test
        void getOrganizationSuccessfully() throws Exception {

            mockMvc.perform(get(OrganizationManagementController.ENDPOINT + "/" + organization.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.organizationId").isNotEmpty())
                    .andExpect(jsonPath("$.organizationName", is(organization.getOrganizationName())))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());

        }


        @DisplayName("Get Organization Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:organizations"})
        @Test
        void getOrganizationNotFoundError() throws Exception {

            mockMvc.perform(get(OrganizationManagementController.ENDPOINT + "/12312312"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));

        }

        @DisplayName("Get Organizations Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:organizations"})
        @Test
        void getOrganizationsSuccessfully() throws Exception {

            mockMvc.perform(get(OrganizationManagementController.ENDPOINT))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..organizationId").isNotEmpty())
                    .andExpect(jsonPath("$..organizationName").isNotEmpty())
                    .andExpect(jsonPath("$..createdAt").isNotEmpty())
                    .andExpect(jsonPath("$..lastModifiedAt").isNotEmpty());

        }
    }

    @DisplayName("Editing Organization")
    @Nested
    class EditingOrganization {
        Organization organization;
        EditingOrganizationDto editingOrganization;

        @BeforeEach
        void setUp() {
            organization = new Organization();
            organization.setOrganizationName(RandomStringUtils.random(10, true, false));

            organizationRepository.save(organization);

            editingOrganization = new EditingOrganizationDto();
            editingOrganization.setOrganizationName(RandomStringUtils.random(10, true, false));
        }

        @DisplayName("Edit Organization Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:organizations"})
        @Test
        void editOrganizationSuccessfully() throws Exception {
            mockMvc.perform(put(OrganizationManagementController.ENDPOINT + "/" + organization.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(editingOrganization)))
                    .andExpect(status().isPermanentRedirect())
                    .andExpect(redirectedUrl(OrganizationManagementController.ENDPOINT + "/" + organization.getId()));


            mockMvc.perform(get(OrganizationManagementController.ENDPOINT + "/" + organization.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.organizationId").isNotEmpty())
                    .andExpect(jsonPath("$.organizationName", is(editingOrganization.getOrganizationName())))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());
        }

        @DisplayName("Update Organization Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:organizations"})
        @Test
        void updateOrganizationNotFoundError() throws Exception {

            mockMvc.perform(put(OrganizationManagementController.ENDPOINT + "/123123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingOrganization)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));

        }

    }

    @DisplayName("Deleting Organization")
    @Nested
    class DeletingOrganization {

        Organization organization;

        @BeforeEach
        void setUp() {
            organization = new Organization();
            organization.setOrganizationName(RandomStringUtils.random(10, true, false));

            organizationRepository.save(organization);
        }

        @DisplayName("Delete Organization Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:organizations"})
        @Test
        void deleteOrganizationSuccessfully() throws Exception {

            mockMvc.perform(delete(OrganizationManagementController.ENDPOINT + "/" + organization.getId()))
                    .andDo(print())
                    .andExpect(status().isNoContent());

        }

        @DisplayName("Delete Organization Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:organizations"})
        @Test
        void deleteOrganizationNotFoundError() throws Exception {

            mockMvc.perform(delete(OrganizationManagementController.ENDPOINT + "/123123"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));

        }


    }
}
