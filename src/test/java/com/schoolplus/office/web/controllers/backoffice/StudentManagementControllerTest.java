package com.schoolplus.office.web.controllers.backoffice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolplus.office.domain.*;
import com.schoolplus.office.repository.*;
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

    @Autowired
    ClassroomRepository classroomRepository;

    @Autowired
    OrganizationRepository organizationRepository;

    Organization organization;

    @BeforeEach
    void setUp() {
        organization = new Organization();
        organization.setOrganizationName(RandomStringUtils.random(10, true, false));

        organizationRepository.save(organization);
    }

    @DisplayName("Creating Student")
    @Nested
    class CreatingStudent {

        CreatingStudentDto creatingStudent;
        Authority authority;
        Role role;
        Parent parent;
        Classroom classRoom;

        @BeforeEach
        void setUp() {

            role = roleRepository.findByRoleName("STUDENT").get();
            authority = authorityRepository.findByAuthorityName("profile:manage").get();

            parent = new Parent();
            parent.setUsername(RandomStringUtils.random(10, true, false));
            parent.setOrganization(organization);

            userRepository.save(parent);

            classRoom = new Classroom();
            classRoom.setClassRoomTag(RandomStringUtils.random(10, true, false));
            classRoom.setOrganization(organization);

            classroomRepository.save(classRoom);

            creatingStudent = new CreatingStudentDto();
            creatingStudent.setUsername(RandomStringUtils.random(10, true, false));
            creatingStudent.setPassword(RandomStringUtils.random(10, true, false));
            creatingStudent.setFirstName(RandomStringUtils.random(10, true, false));
            creatingStudent.setLastName(RandomStringUtils.random(10, true, false));
            creatingStudent.setPhoneNumber(RandomStringUtils.random(11, true, false));
            creatingStudent.setEmail(RandomStringUtils.random(10, true, false));
            creatingStudent.setRoles(List.of(role.getId()));
            creatingStudent.setAuthorities(List.of(authority.getId()));
            creatingStudent.setIsAccountNonLocked(true);
            creatingStudent.setIsAccountNonExpired(true);
            creatingStudent.setIsCredentialsNonExpired(true);
            creatingStudent.setIsEnabled(true);
            creatingStudent.setParents(List.of(parent.getId().toString()));
            creatingStudent.setClassRoomId(classRoom.getId());
            creatingStudent.setOrganizationId(organization.getId());
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
                    .andExpect(jsonPath("$.classRoom.classRoomId", is(classRoom.getId().intValue())))
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

        @DisplayName("Creating Student Grade Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:users:students"})
        @Test
        void creatingStudentGradeNotFoundError() throws Exception {

            creatingStudent.setClassRoomId(31513L); // invalid

            mockMvc.perform(post(StudentManagementController.ENDPOINT)
                            .content(objectMapper.writeValueAsString(creatingStudent))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc())));

        }

    }

    @DisplayName("Editing Student")
    @Nested
    class EditStudent {

        Parent parent;
        Student student;
        EditingStudentDto editStudent;
        Classroom classRoom;

        @BeforeEach
        void setUp() {
            parent = new Parent();
            parent.setUsername(RandomStringUtils.random(10, true, false));
            parent.setOrganization(organization);

            userRepository.save(parent);

            student = new Student();
            student.addParent(parent);
            student.setOrganization(organization);

            student = userRepository.save(student);

            classRoom = new Classroom();
            classRoom.setClassRoomTag(RandomStringUtils.random(10, true, false));
            classRoom.setOrganization(organization);

            classroomRepository.save(classRoom);

            editStudent = new EditingStudentDto();
            editStudent.setClassRoomId(classRoom.getId());
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
                    .andExpect(jsonPath("$.classRoom.classRoomId", is(classRoom.getId().intValue())))
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

        @DisplayName("Edit Student Grade Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:users:students"})
        @Test
        void editStudentGradeNotFoundError() throws Exception {

            editStudent.setClassRoomId(12312312312L);

            mockMvc.perform(put(StudentManagementController.ENDPOINT + "/" + student.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editStudent)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc())));

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
            parent.setOrganization(organization);

            userRepository.save(parent);

            Student student = new Student();
            student.addParent(parent);
            student.setOrganization(organization);

            student = userRepository.save(student);

            mockMvc.perform(get(StudentManagementController.ENDPOINT + "/" + student.getId().toString()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId").isNotEmpty())
                    .andExpect(jsonPath("$.parents[*]..userId", anyOf(hasItem(is(parent.getId().toString())))));

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

