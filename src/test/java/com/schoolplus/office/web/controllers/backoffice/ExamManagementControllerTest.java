package com.schoolplus.office.web.controllers.backoffice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolplus.office.domain.*;
import com.schoolplus.office.repository.*;
import com.schoolplus.office.services.ExamService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ExamManagementControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    ExamTypeRepository examTypeRepository;

    @Autowired
    ExamSkeletonRepository examSkeletonRepository;

    @Autowired
    ExamRepository examRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ClassroomRepository classroomRepository;

    @Autowired
    GradeRepository gradeRepository;

    @Autowired
    ExamResultRepository examResultRepository;

    @Autowired
    ExamService examService;

    Exam exam;
    ExamType examType;
    ExamSkeleton examSkeleton;
    ExamField examField;
    Organization organization;
    Classroom classroom;
    Grade grade;

    @Transactional
    @BeforeEach
    void setUp() {
        organization = new Organization();
        organization.setOrganizationName(RandomStringUtils.random(10, true, false));

        organizationRepository.save(organization);

        examType = new ExamType();
        examType.setExamTypeName(RandomStringUtils.random(10, true, false));
        examType.setOrganization(organization);
        examType.setNumberOfQuestion(120);
        examType.setExamDuration(135);

        examTypeRepository.save(examType);

        examSkeleton = new ExamSkeleton();
        examSkeleton.setExamSkeletonName(RandomStringUtils.random(10, true, false));
        examSkeleton.setOrganization(organization);

        examField = new ExamField();
        examField.setExamFieldName("ExamNo");
        examField.setIsReference(true);
        examField.setReferenceField(ReferenceField.EXAM_NO);

        ExamField examField2 = new ExamField();
        examField2.setExamFieldName("Öğrenci No");
        examField2.setIsReference(true);
        examField2.setReferenceField(ReferenceField.STUDENT_NO);

        ExamField examField3 = new ExamField();
        examField3.setExamFieldName("Sınıf Kodu");
        examField3.setIsReference(true);
        examField3.setReferenceField(ReferenceField.CLASSROOM_NO);

        ExamField examField4 = new ExamField();
        examField4.setExamFieldName("TytPuan");
        examField4.setIsReference(true);
        examField4.setReferenceField(ReferenceField.SORTABLE);

        examSkeleton.addExamField(examField);
        examSkeleton.addExamField(examField2);
        examSkeleton.addExamField(examField3);
        examSkeleton.addExamField(examField4);

        examSkeletonRepository.save(examSkeleton);

        exam = new Exam();
        exam.setExamSkeleton(examSkeleton);
        exam.setExamType(examType);
        exam.setExamName(RandomStringUtils.random(10, true, false));
        exam.setOrganization(organization);

        examRepository.save(exam);

        grade = new Grade();
        grade.setGradeName(RandomStringUtils.random(10, true, false));
        grade.setOrganization(organization);

        gradeRepository.save(grade);

        if (!classroomRepository.existsByClassNumber(526L)) {
            classroom = new Classroom();
            classroom.setOrganization(organization);
            classroom.setClassRoomTag(RandomStringUtils.random(10, true, false));
            classroom.setClassNumber(526L);
            classroom.setGrade(grade);

            classroomRepository.save(classroom);
        } else {
            classroom = classroomRepository.findByClassNumber(526L).get();
            classroom.setGrade(grade);

            classroomRepository.save(classroom);
        }

    }

    @DisplayName("Getting Exam")
    @Nested
    class GettingExam {

        @DisplayName("Get Exam Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void getExamSuccessfully() throws Exception {

            mockMvc.perform(get(ExamManagementController.ENDPOINT + "/" + exam.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.examId").isNotEmpty())
                    .andExpect(jsonPath("$.examName", is(exam.getExamName())))
                    .andExpect(jsonPath("$.examType.examTypeId", is(examType.getId().intValue())))
                    .andExpect(jsonPath("$.examType.numberOfQuestion", is(examType.getNumberOfQuestion())))
                    .andExpect(jsonPath("$.examType.examDuration", is(examType.getExamDuration())))
                    .andExpect(jsonPath("$.examSkeleton.examSkeletonId", is(examSkeleton.getId().intValue())))
                    .andExpect(jsonPath("$.examSkeleton.examSkeletonName", is(examSkeleton.getExamSkeletonName())))
                    .andExpect(jsonPath("$.examSkeleton.examFields..examFieldId", anyOf(hasItem(is(examField.getId().intValue())))))
                    .andExpect(jsonPath("$.examSkeleton.examFields..examFieldName", anyOf(hasItem(is(examField.getExamFieldName())))))
                    .andExpect(jsonPath("$.examSkeleton.examFields..isReference", anyOf(hasItem(is(examField.getIsReference())))))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Get Exams Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void getExamsSuccessfully() throws Exception {

            mockMvc.perform(get(ExamManagementController.ENDPOINT + "?size=100"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..examId").isNotEmpty())
                    .andExpect(jsonPath("$..examName", anyOf(hasItem(is(exam.getExamName())))))
                    .andExpect(jsonPath("$..examType.examTypeId", anyOf(hasItem(is(examType.getId().intValue())))))
                    .andExpect(jsonPath("$..examType.numberOfQuestion", anyOf(hasItem(is(examType.getNumberOfQuestion())))))
                    .andExpect(jsonPath("$..examType.examDuration", anyOf(hasItem(is(examType.getExamDuration())))))
                    .andExpect(jsonPath("$..examSkeleton.examSkeletonId", anyOf(hasItem(is(examSkeleton.getId().intValue())))))
                    .andExpect(jsonPath("$..examSkeleton.examSkeletonName", anyOf(hasItem(is(examSkeleton.getExamSkeletonName())))))
                    .andExpect(jsonPath("$..examSkeleton.examFields..examFieldId", anyOf(hasItem(is(examField.getId().intValue())))))
                    .andExpect(jsonPath("$..examSkeleton.examFields..examFieldName", anyOf(hasItem(is(examField.getExamFieldName())))))
                    .andExpect(jsonPath("$..examSkeleton.examFields..isReference", anyOf(hasItem(is(examField.getIsReference())))))
                    .andExpect(jsonPath("$..createdAt").isNotEmpty())
                    .andExpect(jsonPath("$..lastModifiedAt").isNotEmpty());


        }

        @DisplayName("Get Exams By Organization Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void getExamsByOrganizationSuccessfully() throws Exception {

            mockMvc.perform(get(ExamManagementController.ENDPOINT + "/organization/" + organization.getId() + "?size=100"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..examId").isNotEmpty())
                    .andExpect(jsonPath("$..examName", anyOf(hasItem(is(exam.getExamName())))))
                    .andExpect(jsonPath("$..examType.examTypeId", anyOf(hasItem(is(examType.getId().intValue())))))
                    .andExpect(jsonPath("$..examType.numberOfQuestion", anyOf(hasItem(is(examType.getNumberOfQuestion())))))
                    .andExpect(jsonPath("$..examType.examDuration", anyOf(hasItem(is(examType.getExamDuration())))))
                    .andExpect(jsonPath("$..examSkeleton.examSkeletonId", anyOf(hasItem(is(examSkeleton.getId().intValue())))))
                    .andExpect(jsonPath("$..examSkeleton.examSkeletonName", anyOf(hasItem(is(examSkeleton.getExamSkeletonName())))))
                    .andExpect(jsonPath("$..examSkeleton.examFields..examFieldId", anyOf(hasItem(is(examField.getId().intValue())))))
                    .andExpect(jsonPath("$..examSkeleton.examFields..examFieldName", anyOf(hasItem(is(examField.getExamFieldName())))))
                    .andExpect(jsonPath("$..examSkeleton.examFields..isReference", anyOf(hasItem(is(examField.getIsReference())))))
                    .andExpect(jsonPath("$..createdAt").isNotEmpty())
                    .andExpect(jsonPath("$..lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Get Exam Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void getExamNotFoundError() throws Exception {

            mockMvc.perform(get(ExamManagementController.ENDPOINT + "/12312312"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.EXAM_NOT_FOUND.getDesc())));

        }

        @DisplayName("Get Exam Organization Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void getExamOrganizationNotFoundError() throws Exception {

            mockMvc.perform(get(ExamManagementController.ENDPOINT + "/organization/12312312?size=100"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));

        }

    }

    @DisplayName("Getting Exam Types")
    @Nested
    class GettingExamTypes {

        @DisplayName("Get Exam Type Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void getExamTypeSuccessfully() throws Exception {

            mockMvc.perform(get(ExamManagementController.ENDPOINT + "/types/" + examType.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.examTypeId").isNotEmpty())
                    .andExpect(jsonPath("$.examTypeName", is(examType.getExamTypeName())))
                    .andExpect(jsonPath("$.numberOfQuestion", is(examType.getNumberOfQuestion())))
                    .andExpect(jsonPath("$.examDuration", is(examType.getExamDuration())))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Get Exam Types Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void getExamTypesSuccessfully() throws Exception {

            mockMvc.perform(get(ExamManagementController.ENDPOINT + "/types?size=100"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..examTypeId").isNotEmpty())
                    .andExpect(jsonPath("$..examTypeName", anyOf(hasItem(is(examType.getExamTypeName())))))
                    .andExpect(jsonPath("$..numberOfQuestion", anyOf(hasItem(is(examType.getNumberOfQuestion())))))
                    .andExpect(jsonPath("$..examDuration", anyOf(hasItem(is(examType.getExamDuration())))))
                    .andExpect(jsonPath("$..createdAt").isNotEmpty())
                    .andExpect(jsonPath("$..lastModifiedAt").isNotEmpty());


        }

        @DisplayName("Get Exam Types By Organization Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void getExamTypesByOrganizationSuccessfully() throws Exception {

            mockMvc.perform(get(ExamManagementController.ENDPOINT + "/types/organization/" + organization.getId() + "?size=100"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..examTypeId").isNotEmpty())
                    .andExpect(jsonPath("$..examTypeName", anyOf(hasItem(is(examType.getExamTypeName())))))
                    .andExpect(jsonPath("$..numberOfQuestion", anyOf(hasItem(is(examType.getNumberOfQuestion())))))
                    .andExpect(jsonPath("$..examDuration", anyOf(hasItem(is(examType.getExamDuration())))))
                    .andExpect(jsonPath("$..createdAt").isNotEmpty())
                    .andExpect(jsonPath("$..lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Get Exam Type Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void getExamTypeNotFoundError() throws Exception {
            mockMvc.perform(get(ExamManagementController.ENDPOINT + "/types/12312312321?size=100"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.EXAM_TYPE_NOT_FOUND.getDesc())));

        }

        @DisplayName("Get Exam Type Organization Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void getExamTypeOrganizationNotFoundError() throws Exception {
            mockMvc.perform(get(ExamManagementController.ENDPOINT + "/types/organization/12312312321?size=100"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));
        }

    }

    @DisplayName("Getting Exam Skeletons")
    @Nested
    class GettingExamSkeletons {

        @DisplayName("Get Exam Skeleton Fields Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void getExamSkeletonFieldSuccessfully() throws Exception {

            mockMvc.perform(get(ExamManagementController.ENDPOINT + "/skeletons/" + examSkeleton.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..examSkeletonId").isNotEmpty())
                    .andExpect(jsonPath("$..examSkeletonName", anyOf(hasItem(is(examSkeleton.getExamSkeletonName())))))
                    .andExpect(jsonPath("$..examFields..examFieldName", anyOf(hasItem(is(examField.getExamFieldName())))))
                    .andExpect(jsonPath("$..examFields..isReference", anyOf(hasItem(is(examField.getIsReference())))))
                    .andExpect(jsonPath("$..createdAt").isNotEmpty())
                    .andExpect(jsonPath("$..lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Get Exam Skeleton Fields Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void getExamSkeletonFieldsSuccessfully() throws Exception {

            mockMvc.perform(get(ExamManagementController.ENDPOINT + "/skeletons?size=100"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..examSkeletonId").isNotEmpty())
                    .andExpect(jsonPath("$..examSkeletonName", anyOf(hasItem(is(examSkeleton.getExamSkeletonName())))))
                    .andExpect(jsonPath("$..examFields..examFieldName", anyOf(hasItem(is(examField.getExamFieldName())))))
                    .andExpect(jsonPath("$..examFields..isReference", anyOf(hasItem(is(examField.getIsReference())))))
                    .andExpect(jsonPath("$..createdAt").isNotEmpty())
                    .andExpect(jsonPath("$..lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Get Exam Skeleton Fields By Organization Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void getExamSkeletonFieldsByOrganizationSuccessfully() throws Exception {

            mockMvc.perform(get(ExamManagementController.ENDPOINT + "/skeletons/organization/" + organization.getId() + "?size=100"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..examSkeletonId").isNotEmpty())
                    .andExpect(jsonPath("$..examSkeletonName", anyOf(hasItem(is(examSkeleton.getExamSkeletonName())))))
                    .andExpect(jsonPath("$..examFields..examFieldName", anyOf(hasItem(is(examField.getExamFieldName())))))
                    .andExpect(jsonPath("$..examFields..isReference", anyOf(hasItem(is(examField.getIsReference())))))
                    .andExpect(jsonPath("$..createdAt").isNotEmpty())
                    .andExpect(jsonPath("$..lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Get Exam Skeleton Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void getExamSkeletonFieldNotFoundError() throws Exception {

            mockMvc.perform(get(ExamManagementController.ENDPOINT + "/skeletons/123123123"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.EXAM_SKELETON_NOT_FOUND.getDesc())));

        }

        @DisplayName("Get Exam Skeleton Organization Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void getExamSkeletonOrganizationNotFoundError() throws Exception {

            mockMvc.perform(get(ExamManagementController.ENDPOINT + "/skeletons/organization/123123123"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));

        }

    }

    @DisplayName("Getting Exam Results")
    @Nested
    class GettingExamResults {

        InputStream fileInputStream;
        MockMultipartFile file;
        Student student;
        ExamResultDto examResultDto;

        @Transactional
        @BeforeEach
        void setUp() throws Exception {

            if (userRepository.findByStudentNumber(1076L).isEmpty()) {
                student = new Student();
                student.setOrganization(organization);
                student.setFirstName(RandomStringUtils.random(10, true, false));
                student.setLastName(RandomStringUtils.random(10, true, false));
                student.setStudentNumber(1076L);

                userRepository.save(student);
            } else {
                student = userRepository.findByStudentNumber(1076L).get();
            }

            fileInputStream = new FileInputStream(new File("src/main/resources/exam_data2.xls"));
            file = new MockMultipartFile("result",
                    "exam_data.xls",
                    "application/vnd.ms-excel",
                    fileInputStream);

            examResultDto = examService.createExamResult(exam.getId(), file);
        }

        @DisplayName("Get Exam Result Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void getExamResultSuccessfully() throws Exception {
            mockMvc.perform(get(ExamManagementController.ENDPOINT + "/results/" + examResultDto.getExamResultId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.examResultId").isNotEmpty())
                    .andExpect(jsonPath("$.exam.examId", is(exam.getId().intValue())))
                    .andExpect(jsonPath("$.exam.examName", is(exam.getExamName())))
                    .andExpect(jsonPath("$.exam.examType.examTypeId", is(examType.getId().intValue())))
                    .andExpect(jsonPath("$.exam.examType.examTypeName", is(examType.getExamTypeName())))
                    .andExpect(jsonPath("$.exam.examSkeleton.examSkeletonId", is(examSkeleton.getId().intValue())))
                    .andExpect(jsonPath("$.exam.examSkeleton.examFields..examFieldId", anyOf(hasItem(is(examField.getId().intValue())))))
                    .andExpect(jsonPath("$.exam.examSkeleton.examFields..examFieldName", anyOf(hasItem(is(examField.getExamFieldName())))))
                    .andExpect(jsonPath("$.exam.examSkeleton.examFields..isReference", anyOf(hasItem(is(examField.getIsReference())))))
                    .andExpect(jsonPath("$.exam.examSkeleton.examFields..referenceField", anyOf(hasItem(is(examField.getReferenceField().name())))))
                    .andExpect(jsonPath("$.examResultItems..examResultItemId").isNotEmpty())
                    .andExpect(jsonPath("$.examResultItems..student.userId", anyOf(hasItem(is(student.getId().toString())))))
                    .andExpect(jsonPath("$.examResultItems..resultData").isNotEmpty());
        }

        @DisplayName("Get Exam Results Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void getExamResultsSuccessfully() throws Exception {
            mockMvc.perform(get(ExamManagementController.ENDPOINT + "/results" + "?size=100"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..examResultId").isNotEmpty())
                    .andExpect(jsonPath("$..exam.examId", anyOf(hasItem(is(exam.getId().intValue())))))
                    .andExpect(jsonPath("$..exam.examName", anyOf(hasItem(is(exam.getExamName())))))
                    .andExpect(jsonPath("$..exam.examType.examTypeId", anyOf(hasItem(is(examType.getId().intValue())))))
                    .andExpect(jsonPath("$..exam.examType.examTypeName", anyOf(hasItem(is(examType.getExamTypeName())))))
                    .andExpect(jsonPath("$..exam.examSkeleton.examSkeletonId", anyOf(hasItem(is(examSkeleton.getId().intValue())))))
                    .andExpect(jsonPath("$..exam.examSkeleton.examFields..examFieldId", anyOf(hasItem(is(examField.getId().intValue())))))
                    .andExpect(jsonPath("$..exam.examSkeleton.examFields..examFieldName", anyOf(hasItem(is(examField.getExamFieldName())))))
                    .andExpect(jsonPath("$..exam.examSkeleton.examFields..isReference", anyOf(hasItem(is(examField.getIsReference())))))
                    .andExpect(jsonPath("$..exam.examSkeleton.examFields..referenceField", anyOf(hasItem(is(examField.getReferenceField().name())))));
        }

        @DisplayName("Get Exam Results By Organization Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void getExamResultsByOrganizationSuccessfully() throws Exception {
            mockMvc.perform(get(ExamManagementController.ENDPOINT + "/results/organization/" + organization.getId() + "?size=100"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..examResultId").isNotEmpty())
                    .andExpect(jsonPath("$..exam.examId", anyOf(hasItem(is(exam.getId().intValue())))))
                    .andExpect(jsonPath("$..exam.examName", anyOf(hasItem(is(exam.getExamName())))))
                    .andExpect(jsonPath("$..exam.examType.examTypeId", anyOf(hasItem(is(examType.getId().intValue())))))
                    .andExpect(jsonPath("$..exam.examType.examTypeName", anyOf(hasItem(is(examType.getExamTypeName())))))
                    .andExpect(jsonPath("$..exam.examSkeleton.examSkeletonId", anyOf(hasItem(is(examSkeleton.getId().intValue())))))
                    .andExpect(jsonPath("$..exam.examSkeleton.examFields..examFieldId", anyOf(hasItem(is(examField.getId().intValue())))))
                    .andExpect(jsonPath("$..exam.examSkeleton.examFields..examFieldName", anyOf(hasItem(is(examField.getExamFieldName())))))
                    .andExpect(jsonPath("$..exam.examSkeleton.examFields..isReference", anyOf(hasItem(is(examField.getIsReference())))))
                    .andExpect(jsonPath("$..exam.examSkeleton.examFields..referenceField", anyOf(hasItem(is(examField.getReferenceField().name())))));
        }

        @DisplayName("Get Exam Results By Student Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void getExamResultsByStudentSuccessfully() throws Exception {
            mockMvc.perform(get(ExamManagementController.ENDPOINT + "/results/student/" + student.getId().toString() + "?size=1000000"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..examResultId").isNotEmpty())
                    .andExpect(jsonPath("$..exam.examId", anyOf(hasItem(is(exam.getId().intValue())))))
                    .andExpect(jsonPath("$..exam.examName", anyOf(hasItem(is(exam.getExamName())))))
                    .andExpect(jsonPath("$..exam.examType.examTypeId", anyOf(hasItem(is(examType.getId().intValue())))))
                    .andExpect(jsonPath("$..exam.examType.examTypeName", anyOf(hasItem(is(examType.getExamTypeName())))))
                    .andExpect(jsonPath("$..exam.examSkeleton.examSkeletonId", anyOf(hasItem(is(examSkeleton.getId().intValue())))))
                    .andExpect(jsonPath("$..exam.examSkeleton.examFields..examFieldId", anyOf(hasItem(is(examField.getId().intValue())))))
                    .andExpect(jsonPath("$..exam.examSkeleton.examFields..examFieldName", anyOf(hasItem(is(examField.getExamFieldName())))))
                    .andExpect(jsonPath("$..exam.examSkeleton.examFields..isReference", anyOf(hasItem(is(examField.getIsReference())))))
                    .andExpect(jsonPath("$..exam.examSkeleton.examFields..referenceField", anyOf(hasItem(is(examField.getReferenceField().name())))));
        }

        @DisplayName("Get Exam Results By Classroom Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void getExamResultsByClassroomSuccessfully() throws Exception {
            mockMvc.perform(get(ExamManagementController.ENDPOINT + "/results/classroom/" + classroom.getId() + "?size=1000000"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..examResultId").isNotEmpty())
                    .andExpect(jsonPath("$..exam.examId", anyOf(hasItem(is(exam.getId().intValue())))))
                    .andExpect(jsonPath("$..exam.examName", anyOf(hasItem(is(exam.getExamName())))))
                    .andExpect(jsonPath("$..exam.examType.examTypeId", anyOf(hasItem(is(examType.getId().intValue())))))
                    .andExpect(jsonPath("$..exam.examType.examTypeName", anyOf(hasItem(is(examType.getExamTypeName())))))
                    .andExpect(jsonPath("$..exam.examSkeleton.examSkeletonId", anyOf(hasItem(is(examSkeleton.getId().intValue())))))
                    .andExpect(jsonPath("$..exam.examSkeleton.examFields..examFieldId", anyOf(hasItem(is(examField.getId().intValue())))))
                    .andExpect(jsonPath("$..exam.examSkeleton.examFields..examFieldName", anyOf(hasItem(is(examField.getExamFieldName())))))
                    .andExpect(jsonPath("$..exam.examSkeleton.examFields..isReference", anyOf(hasItem(is(examField.getIsReference())))))
                    .andExpect(jsonPath("$..exam.examSkeleton.examFields..referenceField", anyOf(hasItem(is(examField.getReferenceField().name())))));
        }

        @DisplayName("Get Exam Results By Grade Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void getExamResultsByGradeSuccessfully() throws Exception {
            mockMvc.perform(get(ExamManagementController.ENDPOINT + "/results/classroom/grade/" + grade.getId() + "?size=1000000"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..examResultId").isNotEmpty())
                    .andExpect(jsonPath("$..exam.examId", anyOf(hasItem(is(exam.getId().intValue())))))
                    .andExpect(jsonPath("$..exam.examName", anyOf(hasItem(is(exam.getExamName())))))
                    .andExpect(jsonPath("$..exam.examType.examTypeId", anyOf(hasItem(is(examType.getId().intValue())))))
                    .andExpect(jsonPath("$..exam.examType.examTypeName", anyOf(hasItem(is(examType.getExamTypeName())))))
                    .andExpect(jsonPath("$..exam.examSkeleton.examSkeletonId", anyOf(hasItem(is(examSkeleton.getId().intValue())))))
                    .andExpect(jsonPath("$..exam.examSkeleton.examFields..examFieldId", anyOf(hasItem(is(examField.getId().intValue())))))
                    .andExpect(jsonPath("$..exam.examSkeleton.examFields..examFieldName", anyOf(hasItem(is(examField.getExamFieldName())))))
                    .andExpect(jsonPath("$..exam.examSkeleton.examFields..isReference", anyOf(hasItem(is(examField.getIsReference())))))
                    .andExpect(jsonPath("$..exam.examSkeleton.examFields..referenceField", anyOf(hasItem(is(examField.getReferenceField().name())))));
        }

        @DisplayName("Get Exam Result Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void getExamResultNotFoundError() throws Exception {
            mockMvc.perform(get(ExamManagementController.ENDPOINT + "/results/12312312312"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.EXAM_RESULT_NOT_FOUND.getDesc())));
        }

        @DisplayName("Get Exam Results Student Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void getExamResultsStudentNotFoundError() throws Exception {
            mockMvc.perform(get(ExamManagementController.ENDPOINT + "/results/student/"+ UUID.randomUUID().toString() +"?size=100"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.STUDENT_NOT_FOUND.getDesc())));
        }

        @DisplayName("Get Exam Results Classroom Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void getExamResultsClassroomNotFoundError() throws Exception {
            mockMvc.perform(get(ExamManagementController.ENDPOINT + "/results/classroom/12312312312?size=100"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc())));
        }

        @DisplayName("Get Exam Results Grade Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void getExamResultsGradeNotFoundError() throws Exception {
            mockMvc.perform(get(ExamManagementController.ENDPOINT + "/results/classroom/grade/12312312312?size=100"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.GRADE_NOT_FOUND.getDesc())));
        }

        @DisplayName("Get Exam Results Organization Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void getExamResultsOrganizationNotFoundError() throws Exception {
            mockMvc.perform(get(ExamManagementController.ENDPOINT + "/results/organization/12312312312?size=100"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));
        }

    }

    @DisplayName("Creating Exam")
    @Nested
    class CreatingExam {

        CreatingExamDto creatingExam;
        ExamType examType;
        ExamSkeleton examSkeleton;
        ExamField examField;

        @BeforeEach
        void setUp() {
            examType = new ExamType();
            examType.setExamTypeName(RandomStringUtils.random(10, true, false));
            examType.setOrganization(organization);
            examType.setNumberOfQuestion(120);
            examType.setExamDuration(135);

            examTypeRepository.save(examType);

            examField = new ExamField();
            examField.setExamFieldName(RandomStringUtils.random(10, true, false));
            examField.setIsReference(false);

            examSkeleton = new ExamSkeleton();
            examSkeleton.setExamSkeletonName(RandomStringUtils.random(10, true, false));
            examSkeleton.setOrganization(organization);
            examSkeleton.addExamField(examField);

            examSkeletonRepository.save(examSkeleton);

            creatingExam = new CreatingExamDto();
            creatingExam.setExamName(RandomStringUtils.random(10, true, false));
            creatingExam.setExamTypeId(examType.getId());
            creatingExam.setExamSkeletonId(examSkeleton.getId());
            creatingExam.setOrganizationId(organization.getId());
        }

        @DisplayName("Create Exam Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void createExamSuccessfully() throws Exception {
            mockMvc.perform(post(ExamManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingExam)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.examId").isNotEmpty())
                    .andExpect(jsonPath("$.examName", is(creatingExam.getExamName())))
                    .andExpect(jsonPath("$.examType.examTypeId", is(examType.getId().intValue())))
                    .andExpect(jsonPath("$.examType.numberOfQuestion", is(examType.getNumberOfQuestion())))
                    .andExpect(jsonPath("$.examType.examDuration", is(examType.getExamDuration())))
                    .andExpect(jsonPath("$.examSkeleton.examSkeletonId", is(examSkeleton.getId().intValue())))
                    .andExpect(jsonPath("$.examSkeleton.examSkeletonName", is(examSkeleton.getExamSkeletonName())))
                    .andExpect(jsonPath("$.examSkeleton.examFields..examFieldId", anyOf(hasItem(is(examField.getId().intValue())))))
                    .andExpect(jsonPath("$.examSkeleton.examFields..examFieldName", anyOf(hasItem(is(examField.getExamFieldName())))))
                    .andExpect(jsonPath("$.examSkeleton.examFields..isReference", anyOf(hasItem(is(examField.getIsReference())))))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());
        }

        @DisplayName("Create Exam Type Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void createExamTypeNotFoundError() throws Exception {
            creatingExam.setExamTypeId(12312312312L);

            mockMvc.perform(post(ExamManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingExam)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.EXAM_TYPE_NOT_FOUND.getDesc())));
        }

        @DisplayName("Create Exam Skeleton Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void createExamSkeletonNotFoundError() throws Exception {
            creatingExam.setExamSkeletonId(12312312312L);

            mockMvc.perform(post(ExamManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingExam)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.EXAM_SKELETON_NOT_FOUND.getDesc())));
        }

        @DisplayName("Create Exam Organization Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void createExamOrganizationNotFoundError() throws Exception {
            creatingExam.setOrganizationId(12312312312L);

            mockMvc.perform(post(ExamManagementController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingExam)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));
        }

    }

    @DisplayName("Creating Exam Type")
    @Nested
    class CreatingExamType {

        CreatingExamTypeDto creatingExamType;

        @BeforeEach
        void setUp() {
            creatingExamType = new CreatingExamTypeDto();
            creatingExamType.setExamTypeName(RandomStringUtils.random(10, true, false));
            creatingExamType.setNumberOfQuestion(120);
            creatingExamType.setExamDuration(120);
            creatingExamType.setOrganizationId(organization.getId());
        }

        @DisplayName("Create Exam Type Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void createExamTypeSuccessfully() throws Exception {
            mockMvc.perform(post(ExamManagementController.ENDPOINT + "/types")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingExamType)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.examTypeId").isNotEmpty())
                    .andExpect(jsonPath("$.examTypeName", is(creatingExamType.getExamTypeName())))
                    .andExpect(jsonPath("$.numberOfQuestion", is(creatingExamType.getNumberOfQuestion())))
                    .andExpect(jsonPath("$.examDuration", is(creatingExamType.getExamDuration())))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());
        }

        @DisplayName("Create Exam Type Organization Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void createExamTypeOrganizationNotFoundError() throws Exception {
            creatingExamType.setOrganizationId(12312312L);

            mockMvc.perform(post(ExamManagementController.ENDPOINT + "/types")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingExamType)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));
        }

    }

    @DisplayName("Creating Exam Skeleton")
    @Nested
    class CreatingExamSkeleton {

        CreatingExamSkeletonDto creatingExamSkeleton;
        CreatingExamFieldDto creatingExamField;

        @BeforeEach
        void setUp() {
            creatingExamField = new CreatingExamFieldDto();
            creatingExamField.setExamFieldName(RandomStringUtils.random(10, true, false));
            creatingExamField.setReferenceField(ReferenceField.STUDENT_NO);
            creatingExamField.setIsReference(true);

            creatingExamSkeleton = new CreatingExamSkeletonDto();
            creatingExamSkeleton.setExamSkeletonName(RandomStringUtils.random(10, true, false));
            creatingExamSkeleton.setFields(Set.of(creatingExamField));
        }

        @DisplayName("Create Exam Skeleton Fields Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void createExamSkeletonFieldsSuccessfully() throws Exception {
            creatingExamSkeleton.setOrganizationId(organization.getId());

            mockMvc.perform(post(ExamManagementController.ENDPOINT + "/skeletons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingExamSkeleton)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.examSkeletonId").isNotEmpty())
                    .andExpect(jsonPath("$.examSkeletonName", is(creatingExamSkeleton.getExamSkeletonName())))
                    .andExpect(jsonPath("$.examFields..examFieldName", anyOf(hasItem(is(creatingExamField.getExamFieldName())))))
                    .andExpect(jsonPath("$.examFields..isReference", anyOf(hasItem(is(creatingExamField.getIsReference())))))
                    .andExpect(jsonPath("$.examFields..referenceField", anyOf(hasItem(is(creatingExamField.getReferenceField().name())))))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());
        }

        @DisplayName("Create Exam Skeleton Organization Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void createExamSkeletonOrganizationNotFoundError() throws Exception {
            creatingExamSkeleton.setOrganizationId(12312312312L);

            mockMvc.perform(post(ExamManagementController.ENDPOINT + "/skeletons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(creatingExamSkeleton)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));

        }


    }

    @DisplayName("Creating Exam Result")
    @Nested
    class CreatingExamResult {

        InputStream fileInputStream;
        MockMultipartFile file;
        Student student;

        @BeforeEach
        void setUp() throws IOException {

            if (userRepository.findByStudentNumber(1076L).isEmpty()) {
                student = new Student();
                student.setOrganization(organization);
                student.setFirstName(RandomStringUtils.random(10, true, false));
                student.setLastName(RandomStringUtils.random(10, true, false));
                student.setStudentNumber(1076L);

                userRepository.save(student);
            } else {
                student = userRepository.findByStudentNumber(1076L).get();
            }

            fileInputStream = new FileInputStream(new File("src/main/resources/exam_data2.xls"));
            file = new MockMultipartFile("result",
                    "exam_data.xls",
                    "application/vnd.ms-excel",
                    fileInputStream);
        }

        @DisplayName("Create Exam Results Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void createExamResultsSuccessfully() throws Exception {

            mockMvc.perform(multipart(ExamManagementController.ENDPOINT + "/results/exam/" + exam.getId())
                            .file(file)
                            .contentType("application/vnd.ms-excel"))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.examResultId").isNotEmpty())
                    .andExpect(jsonPath("$.exam.examId", is(exam.getId().intValue())))
                    .andExpect(jsonPath("$.exam.examName", is(exam.getExamName())))
                    .andExpect(jsonPath("$.exam.examType.examTypeId", is(examType.getId().intValue())))
                    .andExpect(jsonPath("$.exam.examType.examTypeName", is(examType.getExamTypeName())))
                    .andExpect(jsonPath("$.exam.examSkeleton.examSkeletonId", is(examSkeleton.getId().intValue())))
                    .andExpect(jsonPath("$.exam.examSkeleton.examFields..examFieldId", anyOf(hasItem(is(examField.getId().intValue())))))
                    .andExpect(jsonPath("$.exam.examSkeleton.examFields..examFieldName", anyOf(hasItem(is(examField.getExamFieldName())))))
                    .andExpect(jsonPath("$.exam.examSkeleton.examFields..isReference", anyOf(hasItem(is(examField.getIsReference())))))
                    .andExpect(jsonPath("$.exam.examSkeleton.examFields..referenceField", anyOf(hasItem(is(examField.getReferenceField().name())))))
                    .andExpect(jsonPath("$.examResultItems..examResultItemId").isNotEmpty())
                    .andExpect(jsonPath("$.examResultItems..student.userId", anyOf(hasItem(is(student.getId().toString())))))
                    .andExpect(jsonPath("$.examResultItems..resultData").isNotEmpty());

        }


        @DisplayName("Create Exam Results Exam Id Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void createExamResultsExamIdNotFoundError() throws Exception {

            mockMvc.perform(multipart(ExamManagementController.ENDPOINT + "/results/exam/12312312312")
                            .file(file)
                            .contentType("application/vnd.ms-excel"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.EXAM_NOT_FOUND.getDesc())));


        }

    }

    @DisplayName("Editing Exam")
    @Nested
    class EditingExam {

        EditingExamDto editingExam;
        ExamType newExamType;
        ExamSkeleton newExamSkeleton;
        ExamField newExamField;
        Organization newOrganization;

        @BeforeEach
        void setUp() {
            newOrganization = new Organization();
            newOrganization.setOrganizationName(RandomStringUtils.random(10, true, false));

            organizationRepository.save(newOrganization);

            newExamType = new ExamType();
            newExamType.setExamTypeName(RandomStringUtils.random(10, true, false));
            newExamType.setOrganization(newOrganization);
            newExamType.setNumberOfQuestion(120);
            newExamType.setExamDuration(135);

            examTypeRepository.save(newExamType);

            newExamField = new ExamField();
            newExamField.setExamFieldName(RandomStringUtils.random(10, true, false));
            newExamField.setIsReference(false);

            newExamSkeleton = new ExamSkeleton();
            newExamSkeleton.setExamSkeletonName(RandomStringUtils.random(10, true, false));
            newExamSkeleton.setOrganization(newOrganization);
            newExamSkeleton.addExamField(newExamField);

            examSkeletonRepository.save(newExamSkeleton);

            editingExam = new EditingExamDto();
            editingExam.setExamName(RandomStringUtils.random(10, true, false));
            editingExam.setExamTypeId(newExamType.getId());
            editingExam.setExamSkeletonId(newExamSkeleton.getId());
            editingExam.setOrganizationId(newOrganization.getId());
        }

        @DisplayName("Edit Exam Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void editExamSuccessfully() throws Exception {

            mockMvc.perform(put(ExamManagementController.ENDPOINT + "/" + exam.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingExam)))
                    .andDo(print())
                    .andExpect(status().isPermanentRedirect())
                    .andExpect(redirectedUrl(ExamManagementController.ENDPOINT + "/" + exam.getId()));

            mockMvc.perform(get(ExamManagementController.ENDPOINT + "/" + exam.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.examId").isNotEmpty())
                    .andExpect(jsonPath("$.examName", is(editingExam.getExamName())))
                    .andExpect(jsonPath("$.examType.examTypeId", is(newExamType.getId().intValue())))
                    .andExpect(jsonPath("$.examType.numberOfQuestion", is(newExamType.getNumberOfQuestion())))
                    .andExpect(jsonPath("$.examType.examDuration", is(newExamType.getExamDuration())))
                    .andExpect(jsonPath("$.examSkeleton.examSkeletonId", is(newExamSkeleton.getId().intValue())))
                    .andExpect(jsonPath("$.examSkeleton.examSkeletonName", is(newExamSkeleton.getExamSkeletonName())))
                    .andExpect(jsonPath("$.examSkeleton.examFields..examFieldId", anyOf(hasItem(is(newExamField.getId().intValue())))))
                    .andExpect(jsonPath("$.examSkeleton.examFields..examFieldName", anyOf(hasItem(is(newExamField.getExamFieldName())))))
                    .andExpect(jsonPath("$.examSkeleton.examFields..isReference", anyOf(hasItem(is(newExamField.getIsReference())))))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Edit Exam Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void editExamNotFoundError() throws Exception {

            mockMvc.perform(put(ExamManagementController.ENDPOINT + "/1231231")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingExam)))
                    .andDo(print())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.EXAM_NOT_FOUND.getDesc())));

        }

        @DisplayName("Edit Exam Type Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void editExamTypeNotFoundError() throws Exception {

            editingExam.setExamTypeId(12312312L);

            mockMvc.perform(put(ExamManagementController.ENDPOINT + "/" + exam.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingExam)))
                    .andDo(print())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.EXAM_TYPE_NOT_FOUND.getDesc())));

        }

        @DisplayName("Edit Exam Skeleton Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void editExamSkeletonNotFoundError() throws Exception {

            editingExam.setExamSkeletonId(12312312L);

            mockMvc.perform(put(ExamManagementController.ENDPOINT + "/" + exam.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingExam)))
                    .andDo(print())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.EXAM_SKELETON_NOT_FOUND.getDesc())));

        }

        @DisplayName("Edit Exam Organization Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void editExamOrganizationNotFoundError() throws Exception {

            editingExam.setOrganizationId(12312312L);

            mockMvc.perform(put(ExamManagementController.ENDPOINT + "/" + exam.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingExam)))
                    .andDo(print())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));

        }

    }

    @DisplayName("Editing Exam Type")
    @Nested
    class EditingExamType {

        EditingExamTypeDto editingExamType;
        Organization newOrganization;

        @BeforeEach
        void setUp() {
            newOrganization = new Organization();
            newOrganization.setOrganizationName(RandomStringUtils.random(10, true, false));

            organizationRepository.save(newOrganization);

            editingExamType = new EditingExamTypeDto();
            editingExamType.setExamTypeName(RandomStringUtils.random(10, true, false));
            editingExamType.setExamDuration(12312312);
            editingExamType.setNumberOfQuestion(12321321);
            editingExamType.setOrganizationId(newOrganization.getId());
        }

        @DisplayName("Edit Exam Type Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void editExamTypeSuccessfully() throws Exception {

            mockMvc.perform(put(ExamManagementController.ENDPOINT + "/types/" + examType.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingExamType)))
                    .andDo(print())
                    .andExpect(status().isPermanentRedirect())
                    .andExpect(redirectedUrl(ExamManagementController.ENDPOINT + "/types/" + examType.getId()));

            mockMvc.perform(get(ExamManagementController.ENDPOINT + "/types/" + examType.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.examTypeId").isNotEmpty())
                    .andExpect(jsonPath("$.examTypeName", is(editingExamType.getExamTypeName())))
                    .andExpect(jsonPath("$.numberOfQuestion", is(editingExamType.getNumberOfQuestion())))
                    .andExpect(jsonPath("$.examDuration", is(editingExamType.getExamDuration())))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Edit Exam Type Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void editExamTypeNotFoundError() throws Exception {
            mockMvc.perform(put(ExamManagementController.ENDPOINT + "/types/12312312")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingExamType)))
                    .andDo(print())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.EXAM_TYPE_NOT_FOUND.getDesc())));
        }

        @DisplayName("Edit Exam Type Organization Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void editExamTypeOrganizationNotFoundError() throws Exception {
            editingExamType.setOrganizationId(123123123L);

            mockMvc.perform(put(ExamManagementController.ENDPOINT + "/types/" + examType.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingExamType)))
                    .andDo(print())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));
        }

    }

    @DisplayName("Editing Exam Skeleton")
    @Nested
    class EditingExamSkeleton {

        Organization newOrganization;
        EditingExamSkeletonDto editingExamSkeleton;
        CreatingExamFieldDto creatingExamField;

        @BeforeEach
        void setUp() {
            newOrganization = new Organization();
            newOrganization.setOrganizationName(RandomStringUtils.random(10, true, false));

            organizationRepository.save(newOrganization);

            creatingExamField = new CreatingExamFieldDto();
            creatingExamField.setExamFieldName(RandomStringUtils.random(10, true, false));
            creatingExamField.setIsReference(false);

            editingExamSkeleton = new EditingExamSkeletonDto();
            editingExamSkeleton.setExamSkeletonName(RandomStringUtils.random(10, true, false));
            editingExamSkeleton.setOrganizationId(newOrganization.getId());
            editingExamSkeleton.setAddedFields(Set.of(creatingExamField));
            editingExamSkeleton.setRemovedFields(Set.of(examField.getId()));
        }

        @DisplayName("Edit Exam Skeleton Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void editExamSkeletonSuccessfully() throws Exception {

            mockMvc.perform(put(ExamManagementController.ENDPOINT + "/skeletons/" + examSkeleton.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingExamSkeleton)))
                    .andDo(print())
                    .andExpect(status().isPermanentRedirect())
                    .andExpect(redirectedUrl(ExamManagementController.ENDPOINT + "/skeletons/" + examSkeleton.getId()));

            mockMvc.perform(get(ExamManagementController.ENDPOINT + "/skeletons/" + examSkeleton.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.examSkeletonId").isNotEmpty())
                    .andExpect(jsonPath("$.examSkeletonName", is(editingExamSkeleton.getExamSkeletonName())))
                    .andExpect(jsonPath("$.examFields..examFieldName", anyOf(hasItem(is(creatingExamField.getExamFieldName())))))
                    .andExpect(jsonPath("$.examFields..isReference", anyOf(hasItem(is(creatingExamField.getIsReference())))))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Edit Exam Skeleton Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void editExamSkeletonNotFoundError() throws Exception {

            mockMvc.perform(put(ExamManagementController.ENDPOINT + "/skeletons/123123123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingExamSkeleton)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.EXAM_SKELETON_NOT_FOUND.getDesc())));

        }

        @DisplayName("Edit Exam Skeleton Organization Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void editExamSkeletonOrganizationNotFoundError() throws Exception {

            editingExamSkeleton.setOrganizationId(123123123L);

            mockMvc.perform(put(ExamManagementController.ENDPOINT + "/skeletons/" + examSkeleton.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingExamSkeleton)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));

        }

        @DisplayName("Edit Exam Skeleton Exam Field Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void editExamSkeletonExamFieldNotFoundError() throws Exception {

            editingExamSkeleton.setRemovedFields(Set.of(12312312312L));

            mockMvc.perform(put(ExamManagementController.ENDPOINT + "/skeletons/" + examSkeleton.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingExamSkeleton)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.EXAM_FIELD_NOT_FOUND.getDesc())));

        }

    }

    @DisplayName("Editing Exam Results")
    @Nested
    class EditingExamResults {

        InputStream fileInputStream;
        MockMultipartFile file;
        Student student;
        ExamResultDto examResultDto;
        EditingExamResultDto editingExamResult;
        Exam newExam;
        Student newStudent;
        ExamResultItemDto examResultItemDto;

        @Transactional
        @BeforeEach
        void setUp() throws Exception {

            if (userRepository.findByStudentNumber(1076L).isEmpty()) {
                student = new Student();
                student.setOrganization(organization);
                student.setFirstName(RandomStringUtils.random(10, true, false));
                student.setLastName(RandomStringUtils.random(10, true, false));
                student.setStudentNumber(1076L);

                userRepository.save(student);
            } else {
                student = userRepository.findByStudentNumber(1076L).get();
            }

            fileInputStream = new FileInputStream(new File("src/main/resources/exam_data2.xls"));
            file = new MockMultipartFile("result",
                    "exam_data.xls",
                    "application/vnd.ms-excel",
                    fileInputStream);

            examResultDto = examService.createExamResult(exam.getId(), file);

            newExam = new Exam();
            newExam.setOrganization(organization);
            newExam.setExamType(examType);
            newExam.setExamSkeleton(examSkeleton);
            newExam.setExamName(RandomStringUtils.random(10, true, false));

            examRepository.save(newExam);

            newStudent = new Student();
            newStudent.setOrganization(organization);

            userRepository.save(newStudent);

            examResultItemDto =
                    ((ExamResultItemDto) examResultDto.getExamResultItems().toArray()[0]);

            CreatingExamResultItemDto creatingExamResultItemDto = new CreatingExamResultItemDto();
            creatingExamResultItemDto.setStudentId(newStudent.getId().toString());
            creatingExamResultItemDto.setClassRoomId(classroom.getId());
            creatingExamResultItemDto.setSortable(new BigDecimal("345.421"));
            creatingExamResultItemDto.setResultData(Map.of("TytPuan", "345,421"));

            editingExamResult = new EditingExamResultDto();
            editingExamResult.setExamId(newExam.getId());
            editingExamResult.setRemovedExamResultItems(List.of(examResultItemDto.getExamResultItemId()));
            editingExamResult.setAddedExamResultItems(List.of(creatingExamResultItemDto));
        }

        @DisplayName("Edit Exam Result Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void editExamResultSuccessfully() throws Exception {
            mockMvc.perform(put(ExamManagementController.ENDPOINT + "/results/" + examResultDto.getExamResultId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(editingExamResult)))
                    .andDo(print())
                    .andExpect(status().isPermanentRedirect())
                    .andExpect(redirectedUrl(ExamManagementController.ENDPOINT + "/results/" + examResultDto.getExamResultId()));

            mockMvc.perform(get(ExamManagementController.ENDPOINT + "/results/" + examResultDto.getExamResultId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.examResultId").isNotEmpty())
                    .andExpect(jsonPath("$.exam.examId", is(newExam.getId().intValue())))
                    .andExpect(jsonPath("$.exam.examName", is(newExam.getExamName())))
                    .andExpect(jsonPath("$.exam.examType.examTypeId", is(examType.getId().intValue())))
                    .andExpect(jsonPath("$.exam.examType.examTypeName", is(examType.getExamTypeName())))
                    .andExpect(jsonPath("$.exam.examSkeleton.examSkeletonId", is(examSkeleton.getId().intValue())))
                    .andExpect(jsonPath("$.exam.examSkeleton.examFields..examFieldId", anyOf(hasItem(is(examField.getId().intValue())))))
                    .andExpect(jsonPath("$.exam.examSkeleton.examFields..examFieldName", anyOf(hasItem(is(examField.getExamFieldName())))))
                    .andExpect(jsonPath("$.exam.examSkeleton.examFields..isReference", anyOf(hasItem(is(examField.getIsReference())))))
                    .andExpect(jsonPath("$.exam.examSkeleton.examFields..referenceField", anyOf(hasItem(is(examField.getReferenceField().name())))))
                    .andExpect(jsonPath("$.examResultItems.length()", is(examResultDto.getExamResultItems().size())))
                    .andExpect(jsonPath("$.examResultItems..examResultItemId").isNotEmpty())
                    .andExpect(jsonPath("$.examResultItems..student.userId", anyOf(hasItem(is(newStudent.getId().toString())))))
                    .andExpect(jsonPath("$.examResultItems..resultData").isNotEmpty());
        }

        @DisplayName("Edit Exam Result Item Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void editExamResultItemSuccessfully() throws Exception {
            ExamResult examResult = new ExamResult();
            examResult.setExam(exam);

            examResultRepository.save(examResult);

            EditingExamResultItemDto editingExamResultItemDto = new EditingExamResultItemDto();
            editingExamResultItemDto.setExamResultId(examResult.getId());
            editingExamResultItemDto.setClassRoomId(classroom.getId());
            editingExamResultItemDto.setSortable(new BigDecimal("232.123"));
            editingExamResultItemDto.setStudentId(newStudent.getId().toString());
            editingExamResultItemDto.setResultData(Map.of("TytPuan",  "323.122"));

            mockMvc.perform(put(ExamManagementController.ENDPOINT + "/results/item/" + examResultItemDto.getExamResultItemId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(editingExamResultItemDto)))
                    .andDo(print())
                    .andExpect(status().isPermanentRedirect())
                    .andExpect(redirectedUrl(ExamManagementController.ENDPOINT + "/results/item/" + examResultItemDto.getExamResultItemId()));

            mockMvc.perform(get(ExamManagementController.ENDPOINT + "/results/item/" + examResultItemDto.getExamResultItemId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.examResultItemId", is(examResultItemDto.getExamResultItemId().intValue())))
                    .andExpect(jsonPath("$.student.userId", is(newStudent.getId().toString())))
                    .andExpect(jsonPath("$.resultData.TytPuan", is("323.122")))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());
        }

        @DisplayName("Edit Exam Result Item Itself Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void editExamResultItemItselfNotFoundError() throws Exception {
            ExamResult examResult = new ExamResult();
            examResult.setExam(exam);

            examResultRepository.save(examResult);

            EditingExamResultItemDto editingExamResultItemDto = new EditingExamResultItemDto();
            editingExamResultItemDto.setExamResultId(examResult.getId());
            editingExamResultItemDto.setClassRoomId(classroom.getId());
            editingExamResultItemDto.setSortable(new BigDecimal("232.123"));
            editingExamResultItemDto.setStudentId(newStudent.getId().toString());
            editingExamResultItemDto.setResultData(Map.of("TytPuan",  "323.122"));

            mockMvc.perform(put(ExamManagementController.ENDPOINT + "/results/item/123123123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingExamResultItemDto)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.EXAM_RESULT_ITEM_NOT_FOUND.getDesc())));
        }

        @DisplayName("Edit Exam Result Item Result Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void editExamResultItemResultNotFoundError() throws Exception {
            ExamResult examResult = new ExamResult();
            examResult.setExam(exam);

            examResultRepository.save(examResult);

            EditingExamResultItemDto editingExamResultItemDto = new EditingExamResultItemDto();
            editingExamResultItemDto.setExamResultId(123123213L);
            editingExamResultItemDto.setClassRoomId(classroom.getId());
            editingExamResultItemDto.setSortable(new BigDecimal("232.123"));
            editingExamResultItemDto.setStudentId(newStudent.getId().toString());
            editingExamResultItemDto.setResultData(Map.of("TytPuan",  "323.122"));

            mockMvc.perform(put(ExamManagementController.ENDPOINT + "/results/item/" + examResultItemDto.getExamResultItemId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingExamResultItemDto)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.EXAM_RESULT_NOT_FOUND.getDesc())));
        }

        @DisplayName("Edit Exam Result Item Classroom Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void editExamResultItemClassroomNotFoundError() throws Exception {
            ExamResult examResult = new ExamResult();
            examResult.setExam(exam);

            examResultRepository.save(examResult);

            EditingExamResultItemDto editingExamResultItemDto = new EditingExamResultItemDto();
            editingExamResultItemDto.setExamResultId(examResult.getId());
            editingExamResultItemDto.setClassRoomId(1231231232L);
            editingExamResultItemDto.setSortable(new BigDecimal("232.123"));
            editingExamResultItemDto.setStudentId(newStudent.getId().toString());
            editingExamResultItemDto.setResultData(Map.of("TytPuan",  "323.122"));

            mockMvc.perform(put(ExamManagementController.ENDPOINT + "/results/item/" + examResultItemDto.getExamResultItemId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingExamResultItemDto)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc())));
        }

        @DisplayName("Edit Exam Result Item Student Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void editExamResultItemStudentNotFoundError() throws Exception {
            ExamResult examResult = new ExamResult();
            examResult.setExam(exam);

            examResultRepository.save(examResult);

            EditingExamResultItemDto editingExamResultItemDto = new EditingExamResultItemDto();
            editingExamResultItemDto.setExamResultId(examResult.getId());
            editingExamResultItemDto.setClassRoomId(classroom.getId());
            editingExamResultItemDto.setSortable(new BigDecimal("232.123"));
            editingExamResultItemDto.setStudentId(UUID.randomUUID().toString());
            editingExamResultItemDto.setResultData(Map.of("TytPuan",  "323.122"));

            mockMvc.perform(put(ExamManagementController.ENDPOINT + "/results/item/" + examResultItemDto.getExamResultItemId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingExamResultItemDto)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.STUDENT_NOT_FOUND.getDesc())));
        }

        @DisplayName("Edit Exam Result Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void editExamResultNotFoundError() throws Exception {
            mockMvc.perform(put(ExamManagementController.ENDPOINT + "/results/12312321")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingExamResult)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.EXAM_RESULT_NOT_FOUND.getDesc())));
        }

        @DisplayName("Edit Exam Result Exam Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void editExamResultExamNotFoundError() throws Exception {
            editingExamResult.setExamId(123123123L);
            mockMvc.perform(put(ExamManagementController.ENDPOINT + "/results/" + examResultDto.getExamResultId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingExamResult)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.EXAM_NOT_FOUND.getDesc())));
        }

        @DisplayName("Edit Exam Result Item Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void editExamResultItemNotFoundError() throws Exception {
            editingExamResult.setRemovedExamResultItems(List.of(12312312L));
            mockMvc.perform(put(ExamManagementController.ENDPOINT + "/results/" + examResultDto.getExamResultId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(editingExamResult)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.EXAM_RESULT_ITEM_NOT_FOUND.getDesc())));
        }

    }

    @DisplayName("Deleting Exam")
    @Nested
    class DeletingExam {

        @DisplayName("Delete Exam Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void deleteExamSuccessfully() throws Exception {
            mockMvc.perform(delete(ExamManagementController.ENDPOINT + "/" + exam.getId()))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }


        @DisplayName("Delete Exam Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void deleteExamNotFoundError() throws Exception {
            mockMvc.perform(delete(ExamManagementController.ENDPOINT + "/12312312"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.EXAM_NOT_FOUND.getDesc())));
        }

    }

    @DisplayName("Deleting Exam Type")
    @Nested
    class DeletingExamType {

        @DisplayName("Delete Exam Type Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void deleteExamTypeSuccessfully() throws Exception {
            mockMvc.perform(delete(ExamManagementController.ENDPOINT + "/types/" + examType.getId()))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @DisplayName("Delete Exam Type Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void deleteExamTypeNotFoundError() throws Exception {
            mockMvc.perform(delete(ExamManagementController.ENDPOINT + "/types/12312312"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.EXAM_TYPE_NOT_FOUND.getDesc())));
        }

    }

    @DisplayName("Deleting Exam Skeleton")
    @Nested
    class DeletingExamSkeleton {

        @DisplayName("Delete Exam Skeleton Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void deleteExamSkeletonSuccessfully() throws Exception {
            mockMvc.perform(delete(ExamManagementController.ENDPOINT + "/skeletons/" + examSkeleton.getId()))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @DisplayName("Delete Exam Skeleton Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void deleteExamSkeletonNotFoundError() throws Exception {
            mockMvc.perform(delete(ExamManagementController.ENDPOINT + "/skeletons/12312312"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.EXAM_SKELETON_NOT_FOUND.getDesc())));
        }

    }

    @DisplayName("Deleting Exam Results")
    @Nested
    class DeletingExamResults {

        InputStream fileInputStream;
        MockMultipartFile file;
        Student student;
        ExamResultDto examResultDto;

        @Transactional
        @BeforeEach
        void setUp() throws Exception {

            if (userRepository.findByStudentNumber(1076L).isEmpty()) {
                student = new Student();
                student.setOrganization(organization);
                student.setFirstName(RandomStringUtils.random(10, true, false));
                student.setLastName(RandomStringUtils.random(10, true, false));
                student.setStudentNumber(1076L);

                userRepository.save(student);
            } else {
                student = userRepository.findByStudentNumber(1076L).get();
            }


            fileInputStream = new FileInputStream(new File("src/main/resources/exam_data2.xls"));
            file = new MockMultipartFile("result",
                    "exam_data.xls",
                    "application/vnd.ms-excel",
                    fileInputStream);

            examResultDto = examService.createExamResult(exam.getId(), file);
        }

        @DisplayName("Delete Exam Results Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void deleteExamResultsSuccessfully() throws Exception {
            mockMvc.perform(delete(ExamManagementController.ENDPOINT + "/results/" + examResultDto.getExamResultId()))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @DisplayName("Delete Exam Results Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:exams"})
        @Test
        void deleteExamResultsNotFoundError() throws Exception {
            mockMvc.perform(delete(ExamManagementController.ENDPOINT + "/results/12312312312"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.EXAM_RESULT_NOT_FOUND.getDesc())));

        }

    }

}
