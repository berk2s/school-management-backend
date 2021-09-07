package com.schoolplus.office.repository;

import com.schoolplus.office.domain.Classroom;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ClassroomRepository extends PagingAndSortingRepository<Classroom, Long> {
}
