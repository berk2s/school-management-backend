package com.schoolplus.office.services;

import com.schoolplus.office.web.models.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ExamService {

    List<ExamDto> getExams(Pageable pageable);

    List<ExamDto> getExamsByOrganization(Long organizationId, Pageable pageable);

    List<ExamTypeDto> getExamTypes(Pageable pageable);

    List<ExamTypeDto> getExamTypesByOrganization(Long organizationId, Pageable pageable);

    List<ExamSkeletonDto> getExamSkeletons(Pageable pageable);

    List<ExamSkeletonDto> getExamSkeletonsByOrganization(Long organizationId, Pageable pageable);

    List<ExamResultDto> getExamResults(Pageable pageable);

    List<ExamResultDto> getExamResultsByOrganization(Long organizationId, Pageable pageable);

    List<ExamResultDto> getExamResultsByStudent(UUID studentId, Pageable pageable);

    List<ExamResultDto> getExamResultsByClassroom(Long classRoomId, Pageable pageable);

    List<ExamResultDto> getExamResultsByGrade(Long gradeId, Pageable pageable);

    ExamDto getExam(Long examId);

    ExamTypeDto getExamType(Long examTypeId);

    ExamSkeletonDto getExamSkeleton(Long examSkeletonId);

    ExamResultDto getExamResult(Long examResultId);

    ExamDto createExam(CreatingExamDto creatingExam);

    ExamSkeletonDto createExamSkeleton(CreatingExamSkeletonDto creatingExamSkeleton);

    ExamTypeDto createExamType(CreatingExamTypeDto creatingExamType);

    ExamResultDto createExamResult(Long examId, MultipartFile result);

    ExamResultItemDto getExamResultItem(Long examResultItemId);

    void updateExam(Long examId, EditingExamDto editingExam);

    void updateExamType(Long examTypeId, EditingExamTypeDto editingExamType);

    void updateExamSkeleton(Long examSkeletonId, EditingExamSkeletonDto editingExamSkeleton);

    void updateExamResult(Long examResultId, EditingExamResultDto editingExamResult);

    void updateExamResultItem(Long examResultItemId, EditingExamResultItemDto editingExamResultItem);

    void deleteExam(Long examId);

    void deleteExamType(Long examTypeId);

    void deleteExamSkeleton(Long examSkeletonId);

    void deleteExamResult(Long examResultId);

}
