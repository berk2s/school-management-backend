package com.schoolplus.office.web.controllers.backoffice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolplus.office.domain.Authority;
import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.domain.Role;
import com.schoolplus.office.domain.User;
import com.schoolplus.office.repository.AuthorityRepository;
import com.schoolplus.office.repository.OrganizationRepository;
import com.schoolplus.office.repository.RoleRepository;
import com.schoolplus.office.repository.UserRepository;
import com.schoolplus.office.web.models.EditingUserDto;
import com.schoolplus.office.web.models.ErrorDesc;
import com.schoolplus.office.web.models.ErrorType;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserManagementControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper objectMapper;

    User user;
    Role role;
    Authority authority;

    @BeforeEach
    void setUp() {
        user = userRepository.findByUsername("username").get();
    }

    @DisplayName("Test Pagination Listing")
    @Nested
    class TestPaging {

        @BeforeEach
        void setUp() {

            for (int i = 0; i < 20; i++) {
                Role role = new Role();
                role.setRoleName(RandomStringUtils.random(10, true, false));

                roleRepository.save(role);

                Authority authority = new Authority();
                authority.setAuthorityName(RandomStringUtils.random(10, true, false));

                authorityRepository.save(authority);

                Organization organization = new Organization();
                organization.setOrganizationName(RandomStringUtils.random(10, true, false));

                organizationRepository.save(organization);

                Hibernate.initialize(role.getUsers());
                Hibernate.initialize(authority.getUsers());

                User user = new User();
                user.setUsername(RandomStringUtils.random(10, true, true));
                user.setPassword(passwordEncoder.encode("password"));
                user.setIsAccountNonLocked(true);
                user.setIsAccountNonExpired(true);
                user.setIsCredentialsNonExpired(true);
                user.setIsEnabled(true);
                user.addRole(role);
                user.addAuthority(authority);
                user.setFirstName("firstName");
                user.setLastName("lastName");
                user.setUsername(RandomStringUtils.random(10, true, false));
                user.setPhoneNumber(RandomStringUtils.random(10, true, false));
                user.setEmail(RandomStringUtils.random(10, true, false));
                user.setOrganization(organization);

                userRepository.save(user);
            }

        }

        @DisplayName("Listing Users Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "list:users"})
        @Test
        void listingUsersSuccessfully() throws Exception {

            mockMvc.perform(get(UserManagementController.ENDPOINT+ "?size=100"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$..userId").isNotEmpty())
                    .andExpect(jsonPath("$..username").isNotEmpty())
                    .andExpect(jsonPath("$..firstName").isNotEmpty())
                    .andExpect(jsonPath("$..lastName").isNotEmpty())
                    .andExpect(jsonPath("$..phoneNumber").isNotEmpty())
                    .andExpect(jsonPath("$..email").isNotEmpty())
                    .andExpect(jsonPath("$..organization").isNotEmpty())
                    .andExpect(jsonPath("$..authorities").isNotEmpty())
                    .andExpect(jsonPath("$..roles").isNotEmpty())
                    .andExpect(jsonPath("$..isEnabled").isNotEmpty())
                    .andExpect(jsonPath("$..isAccountNonExpired").isNotEmpty())
                    .andExpect(jsonPath("$..isAccountNonLocked").isNotEmpty())
                    .andExpect(jsonPath("$..isCredentialsNonExpired").isNotEmpty())
                    .andExpect(jsonPath("$..createdAt").isNotEmpty())
                    .andExpect(jsonPath("$..lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Paging User Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:users"})
        @Test
        void pagingUserSuccessfully() throws Exception {
            int size = 20;
            mockMvc.perform(get(UserManagementController.ENDPOINT + "?size=" + size))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()", is(size)));

        }

    }

    @DisplayName("Test Errors")
    @Nested
    class TestErrors {

        @DisplayName("User not found error when requesting user info")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "read:user"})
        @Test
        void userNotFoundErrorWhenRequestedUserInfo() throws Exception {

            mockMvc.perform(get(UserManagementController.ENDPOINT + "/" + UUID.randomUUID().toString()))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.USER_NOT_FOUND.getDesc())));

        }


        @DisplayName("Organization Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "edit:user", "view:user"})
        @Test
        void organizationNotFoundError() throws Exception {

            EditingUserDto editingUserDto = new EditingUserDto();
            editingUserDto.setOrganizationId(12312312L);

            mockMvc.perform(put(UserManagementController.ENDPOINT + "/" + user.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingUserDto)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));

        }

    }

    @DisplayName("Get User Successfully")
    @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "read:user"})
    @Test
    void getUserSuccessfully() throws Exception {

        mockMvc.perform(get(UserManagementController.ENDPOINT + "/" + user.getId().toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.username").isNotEmpty())
                .andExpect(jsonPath("$.firstName").isNotEmpty())
                .andExpect(jsonPath("$.lastName").isNotEmpty())
                .andExpect(jsonPath("$.phoneNumber").isNotEmpty())
                .andExpect(jsonPath("$.email").isNotEmpty())
                .andExpect(jsonPath("$.organization").isNotEmpty())
                .andExpect(jsonPath("$.authorities").isNotEmpty())
                .andExpect(jsonPath("$.roles").isNotEmpty())
                .andExpect(jsonPath("$.isEnabled").isNotEmpty())
                .andExpect(jsonPath("$.isAccountNonExpired").isNotEmpty())
                .andExpect(jsonPath("$.isAccountNonLocked").isNotEmpty())
                .andExpect(jsonPath("$.isCredentialsNonExpired").isNotEmpty())
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());

    }

    @DisplayName("Edit User Successfully")
    @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "edit:user", "read:user"})
    @Test
    void editUserSuccessfully() throws Exception {

        Organization organization = new Organization();
        organization.setOrganizationName(RandomStringUtils.random(10, true, false));

        organizationRepository.save(organization);

        EditingUserDto editingUserDto = new EditingUserDto();
        editingUserDto.setUsername(RandomStringUtils.random(10, true, false));
        editingUserDto.setOrganizationId(organization.getId());

        mockMvc.perform(put(UserManagementController.ENDPOINT + "/" + user.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editingUserDto)))
                .andDo(print())
                .andExpect(status().isPermanentRedirect())
                .andExpect(redirectedUrl(UserManagementController.ENDPOINT + "/" + user.getId().toString()));

        mockMvc.perform(get(UserManagementController.ENDPOINT + "/" + user.getId().toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.username", is(editingUserDto.getUsername())))
                .andExpect(jsonPath("$.firstName").isNotEmpty())
                .andExpect(jsonPath("$.lastName").isNotEmpty())
                .andExpect(jsonPath("$.phoneNumber").isNotEmpty())
                .andExpect(jsonPath("$.email").isNotEmpty())
                .andExpect(jsonPath("$.organization.organizationName", is(organization.getOrganizationName())))
                .andExpect(jsonPath("$.isEnabled").isNotEmpty())
                .andExpect(jsonPath("$.isAccountNonExpired").isNotEmpty())
                .andExpect(jsonPath("$.isAccountNonLocked").isNotEmpty())
                .andExpect(jsonPath("$.isCredentialsNonExpired").isNotEmpty())
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());

    }

    @DisplayName("Delete User Successfully")
    @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "delete:user"})
    @Test
    void deleteUserSuccessfully() throws Exception {

        mockMvc.perform(delete(UserManagementController.ENDPOINT + "/" + user.getId().toString()))
                .andDo(print())
                .andExpect(status().isNoContent());

    }

}
