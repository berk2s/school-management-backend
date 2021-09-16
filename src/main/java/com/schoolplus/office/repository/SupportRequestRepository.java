package com.schoolplus.office.repository;

import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.domain.SupportRequest;
import com.schoolplus.office.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SupportRequestRepository extends PagingAndSortingRepository<SupportRequest, Long> {

    Page<SupportRequest> findAllByOrganization(Organization organization, Pageable pageable);

    Page<SupportRequest> findAllByOrganizationAndIsSeen(Organization organization, Boolean isSeen, Pageable pageable);

    Page<SupportRequest> findAllByOrganizationAndIsAnonymous(Organization organization, Boolean isAnonymous, Pageable pageable);

    Page<SupportRequest> findAllByOrganizationAndIsAnonymousAndSupportThreads_User(Organization organization,
                                                                                   Boolean isAnonymous,
                                                                                   User user, Pageable pageable);

}
