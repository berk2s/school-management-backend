package com.schoolplus.office.repository;

import com.schoolplus.office.domain.Classroom;
import com.schoolplus.office.domain.Homework;
import com.schoolplus.office.domain.Syllabus;
import com.schoolplus.office.domain.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface HomeworkRepository extends PagingAndSortingRepository<Homework, Long> {

    Page<Homework> findAllByClassroom(Classroom classroom, Pageable pageable);

    Page<Homework> findAllByTeacher(Teacher teacher, Pageable pageable);

    Page<Homework> findAllBySyllabus(Syllabus syllabus, Pageable pageable);

}
