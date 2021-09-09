package com.schoolplus.office.repository;

import com.schoolplus.office.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDateTime;


public interface SyllabusRepository extends PagingAndSortingRepository<Syllabus, Long> {

    Page<Syllabus> findAllByClassroom(Classroom classroom, Pageable pageable);

    Page<Syllabus> findAllByClassroomAndSyllabusStartDateBetween(Classroom classroom, LocalDateTime from, LocalDateTime to, Pageable pageable);

    Page<Syllabus> findAllByLessonAndSyllabusStartDateBetween(Lesson lesson, LocalDateTime from, LocalDateTime to, Pageable pageable);

    Page<Syllabus> findAllByTeacherAndSyllabusStartDateBetween(Teacher teacher, LocalDateTime from, LocalDateTime to, Pageable pageable);

    Page<Syllabus> findAllByOrganizationAndSyllabusStartDateBetween(Organization organization, LocalDateTime from, LocalDateTime to, Pageable pageable);

}