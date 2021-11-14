package com.schoolplus.office.repository;

import com.schoolplus.office.domain.GradeCategory;
import com.schoolplus.office.domain.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GradeCategoryRepository extends PagingAndSortingRepository<GradeCategory, Long> {

    Page<GradeCategory> findAllByOrganization(Organization organization, Pageable pageable);

    Page<GradeCategory> findAllByOrganizationAndGradeCategoryNameStartingWith(Organization organization, String gradeCategoryName, Pageable pageable);

}
