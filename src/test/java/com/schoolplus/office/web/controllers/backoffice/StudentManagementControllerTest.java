package com.schoolplus.office.web.controllers.backoffice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolplus.office.domain.*;
import com.schoolplus.office.repository.AuthorityRepository;
import com.schoolplus.office.repository.RoleRepository;
import com.schoolplus.office.repository.UserRepository;
import com.schoolplus.office.web.models.*;
import lombok.With;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.parameters.P;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class StudentManagementControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @DisplayName("Creating Student")
    @Nested
    class CreatingStudent {

        CreatingStudentDto creatingStudent;
        Authority authority;
        Role role;
        Parent parent;

        @BeforeEach
        void setUp() {
            role = roleRepository.findByRoleName("STUDENT").get();
            authority = authorityRepository.findByAuthorityName("profile:manage").get();

            parent = new Parent();
            parent.setUsername(RandomStringUtils.random(10, true, false));

            userRepository.save(parent);

            creatingStudent = new CreatingStudentDto();
            creatingStudent.setUsername(RandomStringUtils.random(10, true, false));
            creatingStudent.setPassword(RandomStringUtils.random(10, true, false));
            creatingStudent.setFirstName(RandomStringUtils.random(10, true, false));
            creatingStudent.setLastName(RandomStringUtils.random(10, true, false));
            creatingStudent.setPhoneNumber(RandomStringUtils.random(10, true, false));
            creatingStudent.setEmail(RandomStringUtils.random(10, true, false));
            creatingStudent.setRoles(List.of(role.getId()));
            creatingStudent.setAuthorities(List.of(authority.getId()));
            creatingStudent.setIsAccountNonLocked(true);
            creatingStudent.setIsAccountNonExpired(true);
            creatingStudent.setIsCredentialsNonExpired(true);
            creatingStudent.setIsEnabled(true);
            creatingStudent.setGradeType(GradeType.HIGH_SCHOOL);
            creatingStudent.setGradeLevel(GradeLevel.ELEVENTH_GRADE);
            creatingStudent.setParents(List.of(parent.getId().toString()));

        }

        @DisplayName("Creating Student Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:users:students"})
        @Test
        void creatingStudentSuccessfully() throws Exception {

            mockMvc.perform(post(StudentManagementController.ENDPOINT)
                            .content(objectMapper.writeValueAsString(creatingStudent))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.userId").isNotEmpty())
                    .andExpect(jsonPath("$.username", is(creatingStudent.getUsername())))
                    .andExpect(jsonPath("$.firstName", is(creatingStudent.getFirstName())))
                    .andExpect(jsonPath("$.lastName", is(creatingStudent.getLastName())))
                    .andExpect(jsonPath("$.phoneNumber", is(creatingStudent.getPhoneNumber())))
                    .andExpect(jsonPath("$.email", is(creatingStudent.getEmail())))
                    .andExpect(jsonPath("$.authorities[*]", anyOf(hasItem(is(authority.getAuthorityName())))))
                    .andExpect(jsonPath("$.roles", anyOf(hasItem(is(role.getRoleName().toUpperCase(Locale.ROOT))))))
                    .andExpect(jsonPath("$.isEnabled", is(creatingStudent.getIsEnabled())))
                    .andExpect(jsonPath("$.isAccountNonExpired", is(creatingStudent.getIsAccountNonExpired())))
                    .andExpect(jsonPath("$.isAccountNonLocked", is(creatingStudent.getIsAccountNonLocked())))
                    .andExpect(jsonPath("$.isCredentialsNonExpired", is(creatingStudent.getIsAccountNonExpired())))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty())
                    .andExpect(jsonPath("$.gradeType", is(creatingStudent.getGradeType().getType())))
                    .andExpect(jsonPath("$.gradeLevel", is(creatingStudent.getGradeLevel().getGradeYear())))
                    .andExpect(jsonPath("$.parents[*]..username", anyOf(hasItem(is(parent.getUsername())))));
        }

        @DisplayName("Creating Student Parent Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:users:students"})
        @Test
        void creatingStudentParentNotFoundError() throws Exception {

            creatingStudent.setParents(List.of(UUID.randomUUID().toString())); // invalid

            mockMvc.perform(post(StudentManagementController.ENDPOINT)
                            .content(objectMapper.writeValueAsString(creatingStudent))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.PARENT_NOT_FOUND.getDesc())));

        }

        @DisplayName("Creating Student Role Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:users:students"})
        @Test
        void creatingStudentRoleNotFoundError() throws Exception {

            creatingStudent.setRoles(List.of(1231232L)); // invalid

            mockMvc.perform(post(StudentManagementController.ENDPOINT)
                            .content(objectMapper.writeValueAsString(creatingStudent))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ROLE_NOT_FOUND.getDesc())));

        }

        @DisplayName("Creating Student Authority Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:users:students"})
        @Test
        void creatingStudentAuthorityNotFoundError() throws Exception {

            creatingStudent.setAuthorities(List.of(31513L)); // invalid

            mockMvc.perform(post(StudentManagementController.ENDPOINT)
                            .content(objectMapper.writeValueAsString(creatingStudent))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.AUTHORITY_NOT_FOUND.getDesc())));

        }

    }

    @DisplayName("Editing Student")
    @Nested
    class EditStudent {

        Parent parent;
        Student student;
        EditStudentDto editStudent;

        @BeforeEach
        void setUp() {
            parent = new Parent();
            parent.setUsername(RandomStringUtils.random(10, true, false));

            userRepository.save(parent);

            student = new Student();
            student.setGradeType(GradeType.HIGH_SCHOOL);
            student.setGradeLevel(GradeLevel.ELEVENTH_GRADE);
            student.addParent(parent);

            student = userRepository.save(student);

            editStudent = new EditStudentDto();
            editStudent.setGradeType(GradeType.GRADUATED);
        }

        @DisplayName("Edit Student Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:users:students"})
        @Test
        void editStudentSuccessfully() throws Exception {

            mockMvc.perform(put(StudentManagementController.ENDPOINT + "/" + student.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editStudent)))
                    .andExpect(status().isPermanentRedirect())
                    .andExpect(redirectedUrl(StudentManagementController.ENDPOINT + "/" + student.getId().toString()));

            mockMvc.perform(get(StudentManagementController.ENDPOINT + "/" + student.getId().toString()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId").isNotEmpty())
                    .andExpect(jsonPath("$.username", is(student.getUsername())))
                    .andExpect(jsonPath("$.gradeType", is(editStudent.getGradeType().getType())))
                    .andExpect(jsonPath("$.gradeLevel", is(student.getGradeLevel().getGradeYear())))
                    .andExpect(jsonPath("$.parents[*]..username", anyOf(hasItem(is(parent.getUsername())))));
        }

        @DisplayName("Edit Student Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:users:students"})
        @Test
        void editStudentNotFoundError() throws Exception {

            mockMvc.perform(put(StudentManagementController.ENDPOINT + "/" + UUID.randomUUID().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editStudent)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.STUDENT_NOT_FOUND.getDesc())));

        }

        @DisplayName("Edit Student Parent Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:users:students"})
        @Test
        void editStudentParentNotFoundError() throws Exception {

            editStudent.setAddedParents(List.of(UUID.randomUUID().toString()));

            mockMvc.perform(put(StudentManagementController.ENDPOINT + "/" + student.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editStudent)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.PARENT_NOT_FOUND.getDesc())));

        }

    }

    @DisplayName("Getting Student")
    @Nested
    class GettingStudent {

        @DisplayName("Get Student Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:users:students"})
        @Test
        void getStudentSuccessfully() throws Exception {

            Parent parent = new Parent();
            parent.setUsername(RandomStringUtils.random(10, true, false));

            userRepository.save(parent);

            Student student = new Student();
            student.setGradeType(GradeType.HIGH_SCHOOL);
            student.setGradeLevel(GradeLevel.ELEVENTH_GRADE);
            student.addParent(parent);

            student = userRepository.save(student);

            mockMvc.perform(get(StudentManagementController.ENDPOINT + "/" + student.getId().toString()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId").isNotEmpty())
                    .andExpect(jsonPath("$.username", is(student.getUsername())))
                    .andExpect(jsonPath("$.gradeType", is(student.getGradeType().getType())))
                    .andExpect(jsonPath("$.gradeLevel", is(student.getGradeLevel().getGradeYear())))
                    .andExpect(jsonPath("$.parents[*]..username", anyOf(hasItem(is(parent.getUsername())))));

        }

        @DisplayName("Getting Student Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:users:students"})
        @Test
        void gettingStudentNotFoundError() throws Exception {

            mockMvc.perform(get(StudentManagementController.ENDPOINT + "/" + UUID.randomUUID().toString()))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.STUDENT_NOT_FOUND.getDesc())));
        }

    }

}

