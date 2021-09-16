package com.schoolplus.office.repository;

import com.schoolplus.office.domain.Classroom;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface ClassroomRepository extends PagingAndSortingRepository<Classroom, Long> {

    Optional<Classroom> findByClassNumber(Long classNumber);

    boolean existsByClassNumber(Long classNumber);

}

