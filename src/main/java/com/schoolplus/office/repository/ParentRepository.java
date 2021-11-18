package com.schoolplus.office.repository;

import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.domain.Parent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ParentRepository extends PagingAndSortingRepository<Parent, UUID> {

    Page<Parent> findAllByOrganization(Organization organization, Pageable pageable);

    @Query("select p from Parent p where p.organization = :organization and ( lower(p.firstName) like :searchKey% or lower(p.lastName) like :searchKey% or lower(p.username) like :searchKey% )")
    Page<Parent> findAllByOrganizationAndSearchKey(
            @Param("organization") Organization organization,
            @Param("searchKey") String searchKey,
            Pageable pageable
    );
}
