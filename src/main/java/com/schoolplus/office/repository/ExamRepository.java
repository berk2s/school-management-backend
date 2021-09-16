package com.schoolplus.office.repository;

import com.schoolplus.office.domain.Exam;
import com.schoolplus.office.domain.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ExamRepository extends PagingAndSortingRepository<Exam, Long> {

    Page<Exam> findAllByOrganization(Organization organization, Pageable pageable);

}
