package com.schoolplus.office.repository;

import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.domain.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface TeacherRepository extends PagingAndSortingRepository<Teacher, UUID> {

    Page<Teacher> findAllByOrganization(Organization organization, Pageable pageable);

    @Query("select t from Teacher t where t.organization = :organization and ( lower(t.firstName) like :searchKey% or lower(t.lastName) like :searchKey% or lower(t.username) like :searchKey% )")
    Page<Teacher> findAllByOrganizationAndSearchKey(
            @Param("organization") Organization organization,
            @Param("searchKey") String searchKey,
            Pageable pageable
    );

}
