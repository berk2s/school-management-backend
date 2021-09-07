package com.schoolplus.office.repository;

import com.schoolplus.office.domain.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GradeRepository extends PagingAndSortingRepository<Grade, Long> {
}
