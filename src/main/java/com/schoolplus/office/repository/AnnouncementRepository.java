package com.schoolplus.office.repository;

import com.schoolplus.office.domain.Announcement;
import com.schoolplus.office.domain.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AnnouncementRepository extends PagingAndSortingRepository<Announcement, Long> {
    Page<Announcement> findAllByOrganization(Organization organization, Pageable pageable);

    Page<Announcement> findAllByOrganizationAndAnnouncementTitleStartsWith(Organization organization,
                                                                           String announcementTitle,
                                                                           Pageable pageable);
}
