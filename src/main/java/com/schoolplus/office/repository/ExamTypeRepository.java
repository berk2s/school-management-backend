package com.schoolplus.office.repository;

import com.schoolplus.office.domain.ExamType;
import com.schoolplus.office.domain.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ExamTypeRepository extends PagingAndSortingRepository<ExamType, Long> {

    Page<ExamType> findAllByOrganization(Organization organization, Pageable pageable);

}
