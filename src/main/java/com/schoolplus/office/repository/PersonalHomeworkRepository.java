package com.schoolplus.office.repository;

import com.schoolplus.office.domain.Lesson;
import com.schoolplus.office.domain.PersonalHomework;
import com.schoolplus.office.domain.Student;
import com.schoolplus.office.domain.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PersonalHomeworkRepository extends PagingAndSortingRepository<PersonalHomework, Long> {

    Page<PersonalHomework> findAllByStudent(Student student, Pageable pageable);

    Page<PersonalHomework> findAllByTeacher(Teacher teacher, Pageable pageable);

    Page<PersonalHomework> findAllByLesson(Lesson lesson, Pageable pageable);

}
