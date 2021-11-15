package com.schoolplus.office.repository;

import com.schoolplus.office.domain.Lesson;
import com.schoolplus.office.domain.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface LessonRepository extends PagingAndSortingRepository<Lesson, Long> {

    Page<Lesson> findAllByOrganization(Organization organization, Pageable pageable);

    Page<Lesson> findAllByOrganizationAndLessonNameStartingWith(Organization organization, String lessonName, Pageable pageable);

}
