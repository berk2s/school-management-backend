package com.schoolplus.office.web.controllers.backoffice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolplus.office.domain.*;
import com.schoolplus.office.repository.*;
import com.schoolplus.office.web.models.*;
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

            role = new Role();
            role.setRoleName(RandomStringUtils.random(10, true, false));

            roleRepository.save(role);

            authority = new Authority();
            authority.setAuthorityName(RandomStringUtils.random(10, true, false));

            authorityRepository.save(authority);

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
            creatingStudent.setIsAccountNonLocked(true);
            creatingStudent.setIsAccountNonExpired(true);
            creatingStudent.setIsCredentialsNonExpired(true);
            creatingStudent.setIsEnabled(true);
            creatingStudent.setParents(List.of(parent.getId().toString()));
            creatingStudent.setClassRoomId(classRoom.getId());
            creatingStudent.setOrganizationId(organization.getId());
            creatingStudent.setRoles(List.of(role.getId()));
            creatingStudent.setAuthorities(List.of(authority.getId()));
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
                    .andExpect(jsonPath("$.isEnabled", is(creatingStudent.getIsEnabled())))
                    .andExpect(jsonPath("$.isAccountNonExpired", is(creatingStudent.getIsAccountNonExpired())))
                    .andExpect(jsonPath("$.isAccountNonLocked", is(creatingStudent.getIsAccountNonLocked())))
                    .andExpect(jsonPath("$.isCredentialsNonExpired", is(creatingStudent.getIsAccountNonExpired())))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty())
                    .andExpect(jsonPath("$.classRoom.classRoomId", is(classRoom.getId().intValue())));
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
            student.setUsername(RandomStringUtils.random(10,true,false));
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
                    .andExpect(jsonPath("$.classRoom.classRoomId", is(classRoom.getId().intValue())));
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

        Parent parent;
        Student student;
        Authority authority;
        Role role;
        Classroom classroom;
        Grade grade;
        Teacher teacher;

        @Autowired
        AuthorityRepository authorityRepository;

        @Autowired
        RoleRepository roleRepository;

        @Autowired
        ClassroomRepository classroomRepository;

        @Autowired
        GradeRepository gradeRepository;

        @BeforeEach
        void setUp() {

            parent = new Parent();
            parent.setUsername(RandomStringUtils.random(10, true, false));
            parent.setOrganization(organization);

            userRepository.save(parent);

            authority = new Authority();
            authority.setAuthorityName(RandomStringUtils.random(10, true, false));

            authorityRepository.save(authority);

            role = new Role();
            role.setRoleName(RandomStringUtils.random(10, true, false));

            roleRepository.save(role);

            grade = new Grade();
            grade.setOrganization(organization);
            grade.setGradeName(RandomStringUtils.random(10, true, false));

            gradeRepository.save(grade);

            teacher = new Teacher();
            teacher.setOrganization(organization);
            teacher.setUsername(RandomStringUtils.random(10, true, false));
            teacher.setFirstName(RandomStringUtils.random(10, true, false));
            teacher.setLastName(RandomStringUtils.random(10, true, false));

            userRepository.save(teacher);

            classroom = new Classroom();
            classroom.setOrganization(organization);
            classroom.setClassRoomTag(RandomStringUtils.random(10, true, false));
            classroom.setClassNumber(RandomUtils.nextLong());
            classroom.setGrade(grade);
            classroom.setAdvisorTeacher(teacher);

            classroomRepository.save(classroom);

            student = new Student();
            student.setUsername(RandomStringUtils.random(10, true, false));
            student.setFirstName(RandomStringUtils.random(10, true, false));
            student.setLastName(RandomStringUtils.random(10, true, false));
            student.setPhoneNumber(RandomStringUtils.random(10, true, false));
            student.setEmail(RandomStringUtils.random(10, true, false));
            student.addParent(parent);
            student.setStudentNumber(RandomUtils.nextLong());
            student.setClassRoom(classroom);
            student.setOrganization(organization);
            student.setIsEnabled(true);
            student.setIsAccountNonLocked(true);
            student.setIsAccountNonExpired(true);
            student.setIsCredentialsNonExpired(true);
            student.setUserType(UserType.STUDENT);
            student.addAuthority(authority);
            student.addRole(role);

            userRepository.save(student);
        }

        @DisplayName("Get Students By Organization")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:users:students"})
        @Test
        void getStudentsByOrganization() throws Exception {

            mockMvc.perform(get(StudentManagementController.ENDPOINT + "/organization/" + organization.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content..userId").isNotEmpty())
                    .andExpect(jsonPath("$.content..username", anyOf(hasItem(is(student.getUsername())))))
                    .andExpect(jsonPath("$.content..firstName", anyOf(hasItem(is(student.getFirstName())))))
                    .andExpect(jsonPath("$.content..lastName", anyOf(hasItem(is(student.getLastName())))))
                    .andExpect(jsonPath("$.content..phoneNumber",anyOf(hasItem( is(student.getPhoneNumber())))))
                    .andExpect(jsonPath("$.content..email", anyOf(hasItem(is(student.getEmail())))))
                    .andExpect(jsonPath("$.content..studentNumber", anyOf(hasItem(is(student.getStudentNumber())))))
                    .andExpect(jsonPath("$.content..classRoom.classRoomId", anyOf(hasItem(is(classroom.getId().intValue())))))
                    .andExpect(jsonPath("$.content..classRoom.classRoomTag", anyOf(hasItem(is(classroom.getClassRoomTag())))))
                    .andExpect(jsonPath("$.content..classRoom.classNumber", anyOf(hasItem(is(classroom.getClassNumber())))))
                    .andExpect(jsonPath("$.content..classRoom.grade.gradeId", anyOf(hasItem(is(grade.getId().intValue())))))
                    .andExpect(jsonPath("$.content..classRoom.grade.gradeName", anyOf(hasItem(is(grade.getGradeName())))))
                    .andExpect(jsonPath("$.content..classRoom.advisorTeacher.userId", anyOf(hasItem(is(teacher.getId().toString())))))
                    .andExpect(jsonPath("$.content..classRoom.advisorTeacher.firstName", anyOf(hasItem(is(teacher.getFirstName())))))
                    .andExpect(jsonPath("$.content..classRoom.advisorTeacher.lastName", anyOf(hasItem(is(teacher.getLastName())))))
                    .andExpect(jsonPath("$.content..classRoom.organization").isEmpty())
                    .andExpect(jsonPath("$.content..classRoom.grade.organization").isEmpty())
                    .andExpect(jsonPath("$.content..classRoom.advisorTeacher.organization").isEmpty())
                    .andExpect(jsonPath("$.content..authorities[*]", anyOf(hasItem(is(authority.getAuthorityName())))))
                    .andExpect(jsonPath("$.content..roles[*]", anyOf(hasItem(is(role.getRoleName())))))
                    .andExpect(jsonPath("$.content..userType", anyOf(hasItem(is(student.getUserType().name())))))
                    .andExpect(jsonPath("$.content..isEnabled", anyOf(hasItem(is(student.getIsEnabled())))))
                    .andExpect(jsonPath("$.content..isAccountNonExpired", anyOf(hasItem(is(student.getIsAccountNonExpired())))))
                    .andExpect(jsonPath("$.content..isAccountNonLocked", anyOf(hasItem(is(student.getIsAccountNonLocked())))))
                    .andExpect(jsonPath("$.content..isCredentialsNonExpired", anyOf(hasItem(is(student.getIsCredentialsNonExpired())))))
                    .andExpect(jsonPath("$.content..parents..userId", anyOf(hasItem(is(parent.getId().toString())))))
                    .andExpect(jsonPath("$.content..parents..authorities").isEmpty())
                    .andExpect(jsonPath("$.content..parents..roles").isEmpty())
                    .andExpect(jsonPath("$.content..parents..students.length()", anyOf(hasItem(is(0)))))
                    .andExpect(jsonPath("$.content..parents..organization").isEmpty());

        }

        @DisplayName("Get Students By Organization Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:users:students"})
        @Test
        void getStudentsByOrganizationNotFoundError() throws Exception {
            mockMvc.perform(get(StudentManagementController.ENDPOINT + "/organization/123123123123"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));
        }

        @DisplayName("Get Student Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:users:students"})
        @Test
        void getStudentSuccessfully() throws Exception {

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

