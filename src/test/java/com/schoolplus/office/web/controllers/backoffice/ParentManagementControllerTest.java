package com.schoolplus.office.web.controllers.backoffice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolplus.office.domain.Authority;
import com.schoolplus.office.domain.Parent;
import com.schoolplus.office.domain.Role;
import com.schoolplus.office.domain.Student;
import com.schoolplus.office.repository.AuthorityRepository;
import com.schoolplus.office.repository.RoleRepository;
import com.schoolplus.office.repository.UserRepository;
import com.schoolplus.office.web.models.*;
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

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class ParentManagementControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    AuthorityRepository authorityRepository;

    @DisplayName("Creating Parent")
    @Nested
    class CreatingParent {

        Role role;
        Authority authority;
        Student student;
        CreatingParentDto creatingParent;

        @BeforeEach
        void setUp() {
            role = roleRepository.findByRoleName("STUDENT").get();
            authority = authorityRepository.findByAuthorityName("profile:manage").get();

            student = new Student();
            student.setUsername(RandomStringUtils.random(10, true, false));
            student.setPassword(RandomStringUtils.random(10, true, false));
            student.setFirstName(RandomStringUtils.random(10, true, false));
            student.setLastName(RandomStringUtils.random(10, true, false));
            student.setPhoneNumber(RandomStringUtils.random(10, true, false));
            student.setEmail(RandomStringUtils.random(10, true, false));
            student.addRole(role);
            student.addAuthority(authority);
            student.setIsAccountNonLocked(true);
            student.setIsAccountNonExpired(true);
            student.setIsCredentialsNonExpired(true);
            student.setIsEnabled(true);

            userRepository.save(student);

            creatingParent = new CreatingParentDto();
            creatingParent.setUsername(RandomStringUtils.random(10, true, false));
            creatingParent.setPassword(RandomStringUtils.random(10, true, false));
            creatingParent.setFirstName(RandomStringUtils.random(10, true, false));
            creatingParent.setLastName(RandomStringUtils.random(10, true, false));
            creatingParent.setPhoneNumber(RandomStringUtils.random(10, true, false));
            creatingParent.setEmail(RandomStringUtils.random(10, true, false));
            creatingParent.setRoles(List.of(role.getId()));
            creatingParent.setAuthorities(List.of(authority.getId()));
            creatingParent.setIsAccountNonLocked(true);
            creatingParent.setIsAccountNonExpired(true);
            creatingParent.setIsCredentialsNonExpired(true);
            creatingParent.setIsEnabled(true);
            creatingParent.setStudents(List.of(student.getId().toString()));
        }

        @DisplayName("Create Parent Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:users:parents"})
        @Test
        void createParentSuccessfully() throws Exception {

            mockMvc.perform(post(ParentManagementController.ENDPOINT)
                            .content(objectMapper.writeValueAsString(creatingParent))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.students[*]..username", anyOf(hasItem(is(student.getUsername())))));

        }

        @DisplayName("Creating Parent Student Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:users:parents"})
        @Test
        void creatingParentStudentNotFoundError() throws Exception {

            creatingParent.setStudents(List.of(UUID.randomUUID().toString()));

            mockMvc.perform(post(ParentManagementController.ENDPOINT)
                            .content(objectMapper.writeValueAsString(creatingParent))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.STUDENT_NOT_FOUND.getDesc())));


        }

    }

    @DisplayName("Getting Parent")
    @Nested
    class GettingParent {

        Parent parent;
        Student student;

        @BeforeEach
        void setUp() {

            parent = new Parent();
            parent.setUsername("parent_name");

            userRepository.save(parent);

            student = new Student();
            student.setUsername("student_name");
            student.addParent(parent);

            userRepository.save(student);
        }

        @DisplayName("Get Parent Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:users:parents"})
        @Test
        void getParentSuccessfully() throws Exception {

            mockMvc.perform(get(ParentManagementController.ENDPOINT + "/" + parent.getId().toString()))
                    .andDo(print())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId").isNotEmpty())
                    .andExpect(jsonPath("$.username", is(parent.getUsername())))
                    .andExpect(jsonPath("$.students[*]..username", anyOf(hasItem(is(student.getUsername())))));

        }

        @DisplayName("Getting Parent Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:users:parents"})
        @Test
        void gettingParentNotFoundError() throws Exception {

            mockMvc.perform(get(ParentManagementController.ENDPOINT + "/" + UUID.randomUUID().toString()))
                    .andDo(print())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.PARENT_NOT_FOUND.getDesc())));
        }

    }

    @DisplayName("Editing Parent")
    @Nested
    class EditingParent {

        Parent parent;
        Student student;
        EditingParentDto editingParent;

        @BeforeEach
        void setUp() {

            parent = new Parent();
            parent.setUsername("parent_name");

            userRepository.save(parent);

            student = new Student();
            student.setUsername("student_name");
            student.addParent(parent);

            userRepository.save(student);

            editingParent = new EditingParentDto();
            editingParent.setDeletedStudents(List.of(student.getId().toString()));

        }

        @DisplayName("Editing Parent Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:users:parents"})
        @Test
        void editingParentSuccessfully() throws Exception {

            mockMvc.perform(put(ParentManagementController.ENDPOINT + "/" + parent.getId().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(editingParent)))
                    .andDo(print())
                    .andExpect(status().isPermanentRedirect())
                    .andExpect(redirectedUrl(ParentManagementController.ENDPOINT + "/" + parent.getId().toString()));


            mockMvc.perform(get(ParentManagementController.ENDPOINT + "/" + parent.getId().toString()))
                    .andDo(print())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId").isNotEmpty())
                    .andExpect(jsonPath("$.username", is(parent.getUsername())))
                    .andExpect(jsonPath("$.students.length()", is(0)));

        }

        @DisplayName("Editing Parent Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:users:parents"})
        @Test
        void editingParentNotFoundError() throws Exception {
            mockMvc.perform(put(ParentManagementController.ENDPOINT + "/" + UUID.randomUUID().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingParent)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.PARENT_NOT_FOUND.getDesc())));
        }

        @DisplayName("Editing Parent Student Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:users:parents"})
        @Test
        void editingParentStudentNotFoundError() throws Exception {

            editingParent.setDeletedStudents(List.of(UUID.randomUUID().toString()));

            mockMvc.perform(put(ParentManagementController.ENDPOINT + "/" + parent.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingParent)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.STUDENT_NOT_FOUND.getDesc())));
        }

    }



}
