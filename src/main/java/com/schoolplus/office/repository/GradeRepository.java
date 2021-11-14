package com.schoolplus.office.repository;

import com.schoolplus.office.domain.Grade;
import com.schoolplus.office.domain.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GradeRepository extends PagingAndSortingRepository<Grade, Long> {

    Page<Grade> findAllByOrganization(Organization organization, Pageable pageable);

    Page<Grade> findAllByOrganizationAndGradeNameStartsWith(Organization organization, String gradeName, Pageable pageable);

}
