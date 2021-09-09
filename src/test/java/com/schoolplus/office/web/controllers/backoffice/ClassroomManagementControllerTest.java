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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ClassroomManagementControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    ClassroomRepository classroomRepository;

    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    GradeRepository gradeRepository;

    Teacher teacher;
    Student student;
    Parent parent;
    Organization organization;
    Grade grade;

    @BeforeEach
    void setUp() {
        organization = new Organization();
        organization.setOrganizationName(RandomStringUtils.random(10, true, false));

        organizationRepository.save(organization);

        grade = new Grade();
        grade.setGradeName(RandomStringUtils.random(10, true, false));
        grade.setOrganization(organization);

        gradeRepository.save(grade);

        Authority authority = new Authority();
        authority.setAuthorityName(RandomStringUtils.random(10, true, false));

        authorityRepository.save(authority);

        parent = new Parent();
        parent.setUsername(RandomStringUtils.random(10, true, false));
        parent.addAuthority(authority);
        parent.setOrganization(organization);

        teacher = new Teacher();
        teacher.setUsername(RandomStringUtils.random(10, true, false));
        teacher.addAuthority(authority);
        teacher.setOrganization(organization);

        student = new Student();
        student.setUsername(RandomStringUtils.random(10, true, false));
        student.addParent(parent);
        student.addAuthority(authority);
        student.setOrganization(organization);

        userRepository.saveAll(List.of(student, teacher, parent));

    }

    @DisplayName("Creating classRoom")
    @Nested
    class CreatingClassroom {

        CreatingClassroomDto creatingClassRoom;

        @BeforeEach
        void setUp() {
            creatingClassRoom = new CreatingClassroomDto();
            creatingClassRoom.setAdvisorTeacher(teacher.getId().toString());
            creatingClassRoom.setClassRoomId("classRoom tag");
            creatingClassRoom.setStudents(List.of(student.getId().toString()));
            creatingClassRoom.setOrganizationId(organization.getId());
            creatingClassRoom.setGradeId(grade.getId());
        }

        @DisplayName("Create Classroom Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:classrooms"})
        @Test
        void createClassroomSuccessfully() throws Exception {

            mockMvc.perform(post(ClassroomManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingClassRoom)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.classRoomId").isNotEmpty())
                    .andExpect(jsonPath("$.classRoomTag", is(creatingClassRoom.getClassRoomId())))
                    .andExpect(jsonPath("$.grade.gradeId", is(grade.getId().intValue())))
                    .andExpect(jsonPath("$.organization.organizationName", is(organization.getOrganizationName())))
                    .andExpect(jsonPath("$.advisorTeacher.userId", is(teacher.getId().toString())))
                    .andExpect(jsonPath("$.advisorTeacher.username", is(teacher.getUsername())))
                    .andExpect(jsonPath("$.students[*]..userId", anyOf(hasItem(is(student.getId().toString())))))
                    .andExpect(jsonPath("$.students[*]..username", anyOf(hasItem(is(student.getUsername())))));

        }

        @DisplayName("Create Classroom Teacher Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:classrooms"})
        @Test
        void createClassroomTeacherNotFoundError() throws Exception {
            creatingClassRoom.setAdvisorTeacher(UUID.randomUUID().toString());
            mockMvc.perform(post(ClassroomManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingClassRoom)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.TEACHER_NOT_FOUND.getDesc())));
        }

        @DisplayName("Create Classroom Student Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:classrooms"})
        @Test
        void createClassroomStudentNotFoundError() throws Exception {
            creatingClassRoom.setStudents(List.of(UUID.randomUUID().toString()));
            mockMvc.perform(post(ClassroomManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingClassRoom)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.STUDENT_NOT_FOUND.getDesc())));
        }

        @DisplayName("Create Classroom Organization Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:classrooms"})
        @Test
        void createClassroomOrganizationNotFoundError() throws Exception {
            creatingClassRoom.setOrganizationId(123123123L);
            mockMvc.perform(post(ClassroomManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingClassRoom)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));
        }

        @DisplayName("Create Classroom Grade Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:classrooms"})
        @Test
        void createClassroomGradeNotFoundError() throws Exception {
            creatingClassRoom.setGradeId(123123123L);
            mockMvc.perform(post(ClassroomManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingClassRoom)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.GRADE_NOT_FOUND.getDesc())));
        }

    }

    @DisplayName("Editing Classroom")
    @Nested
    class EditingClassroom {

        EditingClassroomDto editingClassroom;
        Classroom classRoom;
        Organization newOrganization;

        @BeforeEach
        void setUp() {
            newOrganization = new Organization();
            newOrganization.setOrganizationName(RandomStringUtils.random(10, true, false));

            organizationRepository.save(newOrganization);

            classRoom = new Classroom();
            classRoom.setAdvisorTeacher(teacher);
            classRoom.addStudent(student);
            classRoom.setOrganization(organization);

            classroomRepository.save(classRoom);

            editingClassroom = new EditingClassroomDto();
            editingClassroom.setClassRoomTag(RandomStringUtils.random(10, true, false));
            editingClassroom.setDeletedStudents(List.of(student.getId().toString()));
            editingClassroom.setClassRoomTag(RandomStringUtils.random(10, true, false));
            editingClassroom.setOrganizationId(newOrganization.getId());
        }

        @DisplayName("Edit Classroom Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:classrooms"})
        @Test
        void editClassroomSuccessfully() throws Exception {

            mockMvc.perform(put(ClassroomManagementController.ENDPOINT + "/" + classRoom.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingClassroom)))
                    .andDo(print())
                    .andExpect(status().isPermanentRedirect())
                    .andExpect(redirectedUrl(ClassroomManagementController.ENDPOINT + "/" + classRoom.getId().toString()));

            mockMvc.perform(get(ClassroomManagementController.ENDPOINT + "/" + classRoom.getId().toString()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.classRoomId").isNotEmpty())
                    .andExpect(jsonPath("$.classRoomTag", is(editingClassroom.getClassRoomTag())))
                    .andExpect(jsonPath("$.organization.organizationName", is(newOrganization.getOrganizationName())))
                    .andExpect(jsonPath("$.advisorTeacher.userId", is(teacher.getId().toString())))
                    .andExpect(jsonPath("$.advisorTeacher.username", is(teacher.getUsername())))
                    .andExpect(jsonPath("$.students.length()", is(0)));

        }

        @DisplayName("Edit Classroom Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:classrooms"})
        @Test
        void editClassroomNotFoundError() throws Exception {
            mockMvc.perform(put(ClassroomManagementController.ENDPOINT + "/12341210")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingClassroom)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc())));
        }

        @DisplayName("Edit Classroom Advisor Teacher Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:classrooms"})
        @Test
        void editClassroomAdvisorTeacherNotFoundError() throws Exception {
            editingClassroom.setAdvisorTeacher(UUID.randomUUID().toString());

            mockMvc.perform(put(ClassroomManagementController.ENDPOINT + "/" + classRoom.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingClassroom)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.TEACHER_NOT_FOUND.getDesc())));
        }

        @DisplayName("Edit Classroom Student Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:classrooms"})
        @Test
        void editClassroomStudentNotFoundError() throws Exception {
            editingClassroom.setAddedStudents(List.of(UUID.randomUUID().toString()));

            mockMvc.perform(put(ClassroomManagementController.ENDPOINT + "/" + classRoom.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingClassroom)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.STUDENT_NOT_FOUND.getDesc())));
        }

        @DisplayName("Edit Classroom Organization Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:classrooms"})
        @Test
        void editClassroomOrganizationNotFoundError() throws Exception {
            editingClassroom.setOrganizationId(12312312L);

            mockMvc.perform(put(ClassroomManagementController.ENDPOINT + "/" + classRoom.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingClassroom)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));
        }

    }

    @DisplayName("Getting Classroom")
    @Nested
    class GettingClassroom {

        Classroom classRoom;
        Organization organization;
        Grade grade;

        @Transactional
        @BeforeEach
        void setUp() {
            organization = new Organization();
            organization.setOrganizationName(RandomStringUtils.random(10, true, false));

            organizationRepository.save(organization);

            classRoom = new Classroom();
            classRoom.setAdvisorTeacher(teacher);
            classRoom.addStudent(student);
            classRoom.setOrganization(organization);
            classRoom.setClassRoomTag(RandomStringUtils.random(10, true, false));

            classroomRepository.save(classRoom);

            grade = new Grade();
            grade.setGradeName(RandomStringUtils.random(10, true, false));
            grade.setOrganization(organization);
            grade.addClassroom(classRoom);

            gradeRepository.save(grade);

        }

        @DisplayName("Get Classroom Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:classrooms"})
        @Test
        void ClassroomsroomSuccessfully() throws Exception {
            mockMvc.perform(get(ClassroomManagementController.ENDPOINT + "/" + classRoom.getId().toString()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.classRoomId").isNotEmpty())
                    .andExpect(jsonPath("$.classRoomTag", is(classRoom.getClassRoomTag())))
                    .andExpect(jsonPath("$.organization.organizationName", is(organization.getOrganizationName())))
                    .andExpect(jsonPath("$.grade.gradeName", is(grade.getGradeName())))
                    .andExpect(jsonPath("$.advisorTeacher.userId", is(teacher.getId().toString())))
                    .andExpect(jsonPath("$.advisorTeacher.username", is(teacher.getUsername())))
                    .andExpect(jsonPath("$.students.length()", is(1)));
        }

        @DisplayName("Get Classroom Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:classrooms"})
        @Test
        void ClassroomsroomNotFoundError() throws Exception {
            mockMvc.perform(get(ClassroomManagementController.ENDPOINT + "/12312312"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc())));
        }
    }

    @DisplayName("Deleting Classroom")
    @Nested
    class DeletingClassroom {

        Classroom classRoom;

        @BeforeEach
        void setUp() {
            classRoom = new Classroom();
            classRoom.setAdvisorTeacher(teacher);
            classRoom.addStudent(student);
            classRoom.setOrganization(organization);

            classroomRepository.save(classRoom);
        }

        @DisplayName("Delete Classroom Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:classrooms"})
        @Test
        void deleteClassroomSuccessfully() throws Exception {
            mockMvc.perform(delete(ClassroomManagementController.ENDPOINT + "/" + classRoom.getId().toString()))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }


        @DisplayName("Delete Classroom Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:classrooms"})
        @Test
        void deleteClassroomNotFoundError() throws Exception {
            mockMvc.perform(delete(ClassroomManagementController.ENDPOINT + "/12312312"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc())));
        }
    }

    @DisplayName("Listing Classrooms")
    @Nested
    class ListingClassrooms {

        @Transactional
        @BeforeEach
        void setUp() {
            for (int i = 0; i < 20; i++) {
                Teacher teacher = new Teacher();
                teacher.setUsername(RandomStringUtils.random(10, true, false));
                teacher.setOrganization(organization);

                Parent parent = new Parent();
                parent.setUsername(RandomStringUtils.random(10, true, false));
                parent.setOrganization(organization);

                Student newStudent = new Student();
                newStudent.setUsername(RandomStringUtils.random(10, true, false));
                newStudent.addParent(parent);
                newStudent.setOrganization(organization);

                Classroom classRoom = new Classroom();
                classRoom.addStudent(newStudent);
                classRoom.setOrganization(organization);
                classRoom.setClassRoomTag(RandomStringUtils.random(10, true, false));

                teacher.addClassroom(classRoom);

                userRepository.saveAll(List.of(teacher, parent, newStudent));
                classroomRepository.save(classRoom);
            }
        }

        @DisplayName("Get Classroom List Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:classrooms"})
        @Test
        void classRoomsListSuccessfully() throws Exception {
            mockMvc.perform(get(ClassroomManagementController.ENDPOINT + "?page=0&size=100"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..classRoomId").isNotEmpty())
                    .andExpect(jsonPath("$..classRoomTag").isNotEmpty())
                    .andExpect(jsonPath("$..advisorTeacher.userId").isNotEmpty())
                    .andExpect(jsonPath("$..advisorTeacher.username").isNotEmpty())
                    .andExpect(jsonPath("$..students..userId").isNotEmpty())
                    .andExpect(jsonPath("$..students..username").isNotEmpty());
        }

    }

}
