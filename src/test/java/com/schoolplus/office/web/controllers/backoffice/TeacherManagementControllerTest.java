package com.schoolplus.office.web.controllers.backoffice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolplus.office.domain.*;
import com.schoolplus.office.repository.*;
import com.schoolplus.office.web.models.CreatingTeacherDto;
import com.schoolplus.office.web.models.EditingTeacherDto;
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
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TeacherManagementControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TeachingSubjectRepository teachingSubjectRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    OrganizationRepository organizationRepository;

    Organization organization;

    Teacher teacher;
    TeachingSubject teachingSubject;

    @BeforeEach
    void setUp() {
        organization = new Organization();
        organization.setOrganizationName(RandomStringUtils.random(10, true, false));

        organizationRepository.save(organization);

        teachingSubject = new TeachingSubject();
        teachingSubject.setSubjectName("A Subject");
        teachingSubject.setOrganization(organization);

        teachingSubjectRepository.save(teachingSubject);

        teacher = new Teacher();
        teacher.setUsername(RandomStringUtils.random(10, true, false));
        teacher.setFirstName(RandomStringUtils.random(10, true, false));
        teacher.setLastName(RandomStringUtils.random(10, true, false));
        teacher.setPhoneNumber(RandomStringUtils.random(10, true, false));
        teacher.addTeachingSubject(teachingSubject);
        teacher.setOrganization(organization);

        teacherRepository.save(teacher);
    }

    @DisplayName("Creating Teacher")
    @Nested
    class CreatingTeacher {

        CreatingTeacherDto creatingTeacher;
        TeachingSubject teachingSubject;
        Role role;
        Authority authority;

        @BeforeEach
        void setUp() {

            role = new Role();
            role.setRoleName(RandomStringUtils.random(10, true, false));

            roleRepository.save(role);

            authority = new Authority();
            authority.setAuthorityName(RandomStringUtils.random(10, true, false));

            authorityRepository.save(authority);

            teachingSubject = teachingSubjectRepository
                    .findBySubjectName("Matematik")
                    .get();

            creatingTeacher = new CreatingTeacherDto();
            creatingTeacher.setUsername(RandomStringUtils.random(10, true, false));
            creatingTeacher.setPassword(RandomStringUtils.random(10, true, false));
            creatingTeacher.setFirstName(RandomStringUtils.random(10, true, false));
            creatingTeacher.setLastName(RandomStringUtils.random(10, true, false));
            creatingTeacher.setPhoneNumber(RandomStringUtils.random(11, true, false));
            creatingTeacher.setEmail(RandomStringUtils.random(10, true, false));
            creatingTeacher.setIsAccountNonLocked(true);
            creatingTeacher.setIsAccountNonExpired(true);
            creatingTeacher.setIsCredentialsNonExpired(true);
            creatingTeacher.setIsEnabled(true);
            creatingTeacher.setTeachingSubjects(List.of(teachingSubject.getId()));
            creatingTeacher.setOrganizationId(organization.getId());
            creatingTeacher.setRoles(List.of(role.getId()));
            creatingTeacher.setAuthorities(List.of(authority.getId()));
        }

        @DisplayName("Create Teacher Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:users:teachers"})
        @Test
        void createTeacherSuccessfully() throws Exception {

            mockMvc.perform(post(TeacherManagementController.ENDPOINT)
                            .content(objectMapper.writeValueAsString(creatingTeacher))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.userId").isNotEmpty())
                    .andExpect(jsonPath("$.username", is(creatingTeacher.getUsername())))
                    .andExpect(jsonPath("$.teachingSubjects[*]..subjectName", anyOf(hasItem(is(teachingSubject.getSubjectName())))));

        }

        @DisplayName("Create Teacher Teaching Subject Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:users:teachers"})
        @Test
        void createTeacherTeachingSubjectNotFoundError() throws Exception {

            creatingTeacher.setTeachingSubjects(List.of(12312L));

            mockMvc.perform(post(TeacherManagementController.ENDPOINT)
                            .content(objectMapper.writeValueAsString(creatingTeacher))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.TEACHING_SUBJECT_NOT_FOUND.getDesc())));

        }

    }

    @DisplayName("Getting Teacher")
    @Nested
    class GettingTeacher {

        @BeforeEach
        void setUp() {

        }

        @DisplayName("Get Teacher Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:users:teachers"})
        @Test
        void getTeacherSuccessfully() throws Exception {

            mockMvc.perform(get(TeacherManagementController.ENDPOINT + "/" + teacher.getId().toString()))
                    .andDo(print())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId").isNotEmpty())
                    .andExpect(jsonPath("$.username", is(teacher.getUsername())))
                    .andExpect(jsonPath("$.teachingSubjects[*]..subjectName", anyOf(hasItem(is(teachingSubject.getSubjectName())))));

        }

        @DisplayName("Get Teachers By Organization Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:users:teachers"})
        @Test
        void getTeachersByOrganizationSuccessfully() throws Exception {

            mockMvc.perform(get(TeacherManagementController.ENDPOINT + "/organization/" + organization.getId()))
                    .andDo(print())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content..userId").isNotEmpty())
                    .andExpect(jsonPath("$.content..username", anyOf(hasItem(is(teacher.getUsername())))))
                    .andExpect(jsonPath("$.content..firstName", anyOf(hasItem(is(teacher.getFirstName())))))
                    .andExpect(jsonPath("$.content..lastName", anyOf(hasItem(is(teacher.getLastName())))))
                    .andExpect(jsonPath("$.content..phoneNumber", anyOf(hasItem(is(teacher.getPhoneNumber())))))
                    .andExpect(jsonPath("$.content..organization").isEmpty())
                    .andExpect(jsonPath("$.content..teachingSubjects..teachingSubjectId", anyOf(hasItem(is(teachingSubject.getId().intValue())))))
                    .andExpect(jsonPath("$.content..teachingSubjects..subjectName", anyOf(hasItem(is(teachingSubject.getSubjectName())))))
                    .andExpect(jsonPath("$.content..createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.content..lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Get Teachers By Organization Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:users:teachers"})
        @Test
        void getTeachersByOrganizationNotFoundError() throws Exception {

            mockMvc.perform(get(TeacherManagementController.ENDPOINT + "/organization/" + RandomUtils.nextLong()))
                    .andDo(print())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));

        }

        @DisplayName("Get Teacher Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:users:teachers"})
        @Test
        void getTeacherNotFoundError() throws Exception {

            mockMvc.perform(get(TeacherManagementController.ENDPOINT + "/" + UUID.randomUUID().toString()))
                    .andDo(print())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.TEACHER_NOT_FOUND.getDesc())));


        }

    }

    @DisplayName("Editing Teacher")
    @Nested
    class EditingTeacher {

        Teacher teacher;
        TeachingSubject teachingSubject;
        EditingTeacherDto editingTeacherDto;

        @BeforeEach
        void setUp() {
            teachingSubject = new TeachingSubject();
            teachingSubject.setSubjectName("A Subject");
            teachingSubject.setOrganization(organization);

            teachingSubjectRepository.save(teachingSubject);

            teacher = new Teacher();
            teacher.setUsername(RandomStringUtils.random(10, true, false));
            teacher.addTeachingSubject(teachingSubject);
            teacher.setOrganization(organization);

            userRepository.save(teacher);

            editingTeacherDto = new EditingTeacherDto();

            editingTeacherDto.setRemoveTeachingSubjects(List.of(teachingSubject.getId()));
        }

        @DisplayName("Edit Teacher Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:users:teachers"})
        @Test
        void editTeacherSuccessfully() throws Exception {

            mockMvc.perform(put(TeacherManagementController.ENDPOINT + "/" + teacher.getId().toString())
                            .content(objectMapper.writeValueAsString(editingTeacherDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isPermanentRedirect())
                    .andExpect(redirectedUrl(TeacherManagementController.ENDPOINT + "/" + teacher.getId().toString()));

            mockMvc.perform(get(TeacherManagementController.ENDPOINT + "/" + teacher.getId().toString()))
                    .andDo(print())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId").isNotEmpty())
                    .andExpect(jsonPath("$.username", is(teacher.getUsername())))
                    .andExpect(jsonPath("$.teachingSubjects.length()", is(0)));

        }

        @DisplayName("Edit Teacher Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:users:teachers"})
        @Test
        void editTeacherNotFoundError() throws Exception {

            mockMvc.perform(put(TeacherManagementController.ENDPOINT + "/" + UUID.randomUUID().toString())
                            .content(objectMapper.writeValueAsString(editingTeacherDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.TEACHER_NOT_FOUND.getDesc())));

        }

        @DisplayName("Edit Teacher Teaching Subject Not Found Error")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:users:teachers"})
        @Test
        void editTeacherTeachibgSubjectNotFoundError() throws Exception {

            editingTeacherDto.setRemoveTeachingSubjects(List.of(123123L));

            mockMvc.perform(put(TeacherManagementController.ENDPOINT + "/" + teacher.getId().toString())
                            .content(objectMapper.writeValueAsString(editingTeacherDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.TEACHING_SUBJECT_NOT_FOUND.getDesc())));

        }

    }
}
