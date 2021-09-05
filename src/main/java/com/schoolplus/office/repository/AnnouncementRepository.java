package com.schoolplus.office.repository;

import com.schoolplus.office.domain.Announcement;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AnnouncementRepository extends PagingAndSortingRepository<Announcement, Long> {
}
