package com.schoolplus.office.repository;

import com.schoolplus.office.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface ContinuityRepository extends PagingAndSortingRepository<Continuity, UUID> {

    Page<Continuity> findAllBySyllabus(Syllabus syllabus, Pageable pageable);

    Page<Continuity> findAllByClassroom(Classroom classroom, Pageable pageable);

    Page<Continuity> findAllByStudent(Student student, Pageable pageable);

    Page<Continuity> findAllByOrganization(Organization organization, Pageable pageable);

}
