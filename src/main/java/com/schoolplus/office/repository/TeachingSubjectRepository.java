package com.schoolplus.office.repository;

import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.domain.TeachingSubject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface TeachingSubjectRepository extends PagingAndSortingRepository<TeachingSubject, Long> {

    Optional<TeachingSubject> findBySubjectName(String subjectName);

    Page<TeachingSubject> findAllByOrganization(Organization organization, Pageable pageable);

    Page<TeachingSubject> findAllByOrganizationAndSubjectNameStartingWith(Organization organization, String subjectName, Pageable pageable);

}
