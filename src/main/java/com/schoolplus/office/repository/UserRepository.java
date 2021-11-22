package com.schoolplus.office.repository;

import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.domain.Student;
import com.schoolplus.office.domain.Teacher;
import com.schoolplus.office.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends PagingAndSortingRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<Student> findByStudentNumber(Long studentNumber);

    Page<Student> findAllStudentsByOrganization(Organization organization, Pageable pageable);

    @Query("select s from Student s where s.organization = :organization and ( lower(s.firstName) like :searchKey% or lower(s.lastName) like :searchKey% or lower(s.username) like :searchKey% or cast(s.studentNumber as string) like :searchKey%)")
    Page<Student> findAllStudentsByOrganizationAndSearchKey(
            @Param("organization") Organization organization,
            @Param("searchKey") String searchKey,
            Pageable pageable
    );

    boolean existsByUsername(String givenData);

    boolean existsByPhoneNumber(String givenData);

    boolean existsByEmail(String givenData);

}
