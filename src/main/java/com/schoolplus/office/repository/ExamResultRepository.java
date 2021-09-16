package com.schoolplus.office.repository;

import com.schoolplus.office.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface ExamResultRepository extends PagingAndSortingRepository<ExamResult, Long> {

    Page<ExamResult> findAllByExamOrganization(Organization organization, Pageable pageable);

    Page<ExamResult> findAllByExamResultItems_Student(Student student, Pageable pageable);

    Page<ExamResult> findAllByExamResultItems_Classroom(Classroom classroom, Pageable pageable);

    Page<ExamResult> findAllByExamResultItems_Classroom_Grade(Grade grade, Pageable pageable);

    Page<ExamResult> findAllByExam(Exam exam, Pageable pageable);

}
