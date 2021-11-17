package com.schoolplus.office.repository;

import com.schoolplus.office.domain.Classroom;
import com.schoolplus.office.domain.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface ClassroomRepository extends PagingAndSortingRepository<Classroom, Long> {

    Optional<Classroom> findByClassNumber(Long classNumber);

    boolean existsByClassNumber(Long classNumber);

    Page<Classroom> findAllByOrganization(Organization organization, Pageable pageable);

    Page<Classroom> findAllByOrganizationAndClassRoomTagStartingWith(Organization organization, String search, Pageable pageable);

}

