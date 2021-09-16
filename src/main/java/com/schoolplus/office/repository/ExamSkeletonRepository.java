package com.schoolplus.office.repository;

import com.schoolplus.office.domain.ExamSkeleton;
import com.schoolplus.office.domain.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ExamSkeletonRepository extends PagingAndSortingRepository<ExamSkeleton, Long> {

    Page<ExamSkeleton> findAllByOrganization(Organization organization, Pageable pageable);

}
