package com.schoolplus.office.web.controllers.backoffice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolplus.office.domain.*;
import com.schoolplus.office.repository.AuthorityRepository;
import com.schoolplus.office.repository.GradeRepository;
import com.schoolplus.office.repository.UserRepository;
import com.schoolplus.office.utils.GradeUtils;
import com.schoolplus.office.web.models.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
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
public class GradeManagementControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    GradeRepository gradeRepository;

    Teacher teacher;
    Student student;
    Parent parent;

    @BeforeEach
    void setUp() {
        Authority authority = new Authority();
        authority.setAuthorityName(RandomStringUtils.random(10, true, false));

        authorityRepository.save(authority);

        parent = new Parent();
        parent.setUsername(RandomStringUtils.random(10, true, false));
        parent.addAuthority(authority);

        teacher = new Teacher();
        teacher.setUsername(RandomStringUtils.random(10, true, false));
        teacher.addAuthority(authority);

        student = new Student();
        student.setGradeType(GradeType.HIGH_SCHOOL);
        student.setGradeLevel(GradeLevel.EIGHTH_GRADE);
        student.setUsername(RandomStringUtils.random(10, true, false));
        student.addParent(parent);
        student.addAuthority(authority);

        userRepository.saveAll(List.of(student, teacher, parent));

    }

    @DisplayName("Creating Grade")
    @Nested
    class CreatingGrade {

        CreatingGradeDto creatingGrade;

        @BeforeEach
        void setUp() {
            creatingGrade = new CreatingGradeDto();
            creatingGrade.setGradeLevel(GradeLevel.ELEVENTH_GRADE);
            creatingGrade.setAdvisorTeacher(teacher.getId().toString());
            creatingGrade.setGradeTag("grade tag");
            creatingGrade.setStudents(List.of(student.getId().toString()));
        }

        @DisplayName("Create Grade Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:grades"})
        @Test
        void createGradeSuccessfully() throws Exception {

            mockMvc.perform(post(GradeManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingGrade)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.gradeId").isNotEmpty())
                    .andExpect(jsonPath("$.gradeType", is(GradeUtils.levelConverter(creatingGrade.getGradeLevel()).getType())))
                    .andExpect(jsonPath("$.gradeLevel", is(creatingGrade.getGradeLevel().getGradeYear())))
                    .andExpect(jsonPath("$.gradeTag", is(creatingGrade.getGradeTag())))
                    .andExpect(jsonPath("$.advisorTeacher.userId", is(teacher.getId().toString())))
                    .andExpect(jsonPath("$.advisorTeacher.username", is(teacher.getUsername())))
                    .andExpect(jsonPath("$.students[*]..userId", anyOf(hasItem(is(student.getId().toString())))))
                    .andExpect(jsonPath("$.students[*]..username", anyOf(hasItem(is(student.getUsername())))));

        }

        @DisplayName("Create Grade Teacher Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:grades"})
        @Test
        void createGradeTeacherNotFoundError() throws Exception {
            creatingGrade.setAdvisorTeacher(UUID.randomUUID().toString());
            mockMvc.perform(post(GradeManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingGrade)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.TEACHER_NOT_FOUND.getDesc())));
        }

        @DisplayName("Create Grade Student Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:grades"})
        @Test
        void createGradeStudentNotFoundError() throws Exception {
            creatingGrade.setStudents(List.of(UUID.randomUUID().toString()));
            mockMvc.perform(post(GradeManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingGrade)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.STUDENT_NOT_FOUND.getDesc())));
        }
    }

    @DisplayName("Editing Grade")
    @Nested
    class EditingGrade {

        EditingGradeDto editingGrade;
        Grade grade;

        @BeforeEach
        void setUp() {
            grade = new Grade();
            grade.setAdvisorTeacher(teacher);
            grade.setGradeType(GradeUtils.levelConverter(GradeLevel.ELEVENTH_GRADE));
            grade.setGradeLevel(GradeLevel.ELEVENTH_GRADE);
            grade.addStudent(student);

            gradeRepository.save(grade);

            editingGrade = new EditingGradeDto();
            editingGrade.setGradeTag(RandomStringUtils.random(10, true, false));
            editingGrade.setGradeLevel(GradeLevel.FIFTH_GRADE.name());
            editingGrade.setDeletedStudents(List.of(student.getId().toString()));
            editingGrade.setGradeTag(RandomStringUtils.random(10, true, false));
        }

        @DisplayName("Edit Grade Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:grades"})
        @Test
        void editGradeSuccessfully() throws Exception {

            mockMvc.perform(put(GradeManagementController.ENDPOINT + "/" + grade.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingGrade)))
                    .andDo(print())
                    .andExpect(status().isPermanentRedirect())
                    .andExpect(redirectedUrl(GradeManagementController.ENDPOINT + "/" + grade.getId().toString()));

            mockMvc.perform(get(GradeManagementController.ENDPOINT + "/" + grade.getId().toString()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.gradeId").isNotEmpty())
                    .andExpect(jsonPath("$.gradeType", is(GradeUtils.levelConverter(GradeLevel.valueOf(editingGrade.getGradeLevel())).getType())))
                    .andExpect(jsonPath("$.gradeLevel", is(GradeLevel.valueOf(editingGrade.getGradeLevel()).getGradeYear())))
                    .andExpect(jsonPath("$.gradeTag", is(editingGrade.getGradeTag())))
                    .andExpect(jsonPath("$.advisorTeacher.userId", is(teacher.getId().toString())))
                    .andExpect(jsonPath("$.advisorTeacher.username", is(teacher.getUsername())))
                    .andExpect(jsonPath("$.students.length()", is(0)));

        }

        @DisplayName("Edit Grade Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:grades"})
        @Test
        void editGradeNotFoundError() throws Exception {
            mockMvc.perform(put(GradeManagementController.ENDPOINT + "/12341210")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingGrade)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.GRADE_NOT_FOUND.getDesc())));
        }

        @DisplayName("Edit Grade Advisor Teacher Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:grades"})
        @Test
        void editGradeAdvisorTeacherNotFoundError() throws Exception {
            editingGrade.setAdvisorTeacher(UUID.randomUUID().toString());

            mockMvc.perform(put(GradeManagementController.ENDPOINT + "/" + grade.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingGrade)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.TEACHER_NOT_FOUND.getDesc())));
        }

        @DisplayName("Edit Grade Student Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:grades"})
        @Test
        void editGradeAdvisorStudentNotFoundError() throws Exception {
            editingGrade.setAddedStudents(List.of(UUID.randomUUID().toString()));

            mockMvc.perform(put(GradeManagementController.ENDPOINT + "/" + grade.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingGrade)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.STUDENT_NOT_FOUND.getDesc())));
        }
    }

    @DisplayName("Getting Grade")
    @Nested
    class GettingGrade {

        Grade grade;

        @BeforeEach
        void setUp() {
            grade = new Grade();
            grade.setAdvisorTeacher(teacher);
            grade.setGradeType(GradeUtils.levelConverter(GradeLevel.ELEVENTH_GRADE));
            grade.setGradeLevel(GradeLevel.ELEVENTH_GRADE);
            grade.addStudent(student);

            gradeRepository.save(grade);
        }

        @DisplayName("Get Grade Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:grades"})
        @Test
        void getGradeSuccessfully() throws Exception {
            mockMvc.perform(get(GradeManagementController.ENDPOINT + "/" + grade.getId().toString()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.gradeId").isNotEmpty())
                    .andExpect(jsonPath("$.gradeType", is(grade.getGradeType().getType())))
                    .andExpect(jsonPath("$.gradeLevel", is(grade.getGradeLevel().getGradeYear())))
                    .andExpect(jsonPath("$.gradeTag", is(grade.getGradeTag())))
                    .andExpect(jsonPath("$.advisorTeacher.userId", is(teacher.getId().toString())))
                    .andExpect(jsonPath("$.advisorTeacher.username", is(teacher.getUsername())))
                    .andExpect(jsonPath("$.students.length()", is(1)));
        }

        @DisplayName("Get Grade Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:grades"})
        @Test
        void getGradeNotFoundError() throws Exception {
            mockMvc.perform(get(GradeManagementController.ENDPOINT + "/12312312"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.GRADE_NOT_FOUND.getDesc())));
        }
    }

    @DisplayName("Deleting Grade")
    @Nested
    class DeletingGrade {

        Grade grade;

        @BeforeEach
        void setUp() {
            grade = new Grade();
            grade.setAdvisorTeacher(teacher);
            grade.setGradeType(GradeUtils.levelConverter(GradeLevel.ELEVENTH_GRADE));
            grade.setGradeLevel(GradeLevel.ELEVENTH_GRADE);
            grade.addStudent(student);

            gradeRepository.save(grade);
        }

        @DisplayName("Delete Grade Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:grades"})
        @Test
        void deleteGradeSuccessfully() throws Exception {
            mockMvc.perform(delete(GradeManagementController.ENDPOINT + "/" + grade.getId().toString()))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }


        @DisplayName("Delete Grade Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:grades"})
        @Test
        void deleteGradeNotFoundError() throws Exception {
            mockMvc.perform(delete(GradeManagementController.ENDPOINT + "/12312312"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.GRADE_NOT_FOUND.getDesc())));
        }
    }

    @DisplayName("Listing Grades")
    @Nested
    class ListingGrades {

        @BeforeEach
        void setUp() {
            for (int i = 0; i < 20; i++) {
                Teacher teacher = new Teacher();
                teacher.setUsername(RandomStringUtils.random(10, true, false));

                Parent parent = new Parent();
                parent.setUsername(RandomStringUtils.random(10, true, false));

                Student newStudent = new Student();
                newStudent.setUsername(RandomStringUtils.random(10, true, false));
                newStudent.addParent(parent);
                newStudent.setGradeType(GradeType.HIGH_SCHOOL);
                newStudent.setGradeLevel(GradeLevel.EIGHTH_GRADE);

                userRepository.saveAll(List.of(teacher, parent, newStudent));

                Grade grade = new Grade();
                grade.setAdvisorTeacher(teacher);
                grade.setGradeType(GradeUtils.levelConverter(GradeLevel.ELEVENTH_GRADE));
                grade.setGradeLevel(GradeLevel.ELEVENTH_GRADE);
                grade.addStudent(newStudent);

                gradeRepository.save(grade);
            }
        }

        @DisplayName("Get Grade List Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:grades"})
        @Test
        void getGradeListSuccessfully() throws Exception {
            mockMvc.perform(get(GradeManagementController.ENDPOINT + "?page=1&size=10"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..gradeId").isNotEmpty())
                    .andExpect(jsonPath("$..gradeType").isNotEmpty())
                    .andExpect(jsonPath("$..gradeLevel").isNotEmpty())
                    .andExpect(jsonPath("$..gradeTag").isNotEmpty())
                    .andExpect(jsonPath("$..advisorTeacher.userId").isNotEmpty())
                    .andExpect(jsonPath("$..advisorTeacher.username").isNotEmpty())
                    .andExpect(jsonPath("$..students..userId").isNotEmpty())
                    .andExpect(jsonPath("$..students..username").isNotEmpty());
        }

    }

}
