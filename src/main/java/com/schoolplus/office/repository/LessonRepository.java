package com.schoolplus.office.repository;

import com.schoolplus.office.domain.Lesson;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface LessonRepository extends PagingAndSortingRepository<Lesson, Long> {
}
