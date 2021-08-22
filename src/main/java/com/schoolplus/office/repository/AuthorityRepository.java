package com.schoolplus.office.repository;

import com.schoolplus.office.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    Optional<Authority> findByAuthorityName(String authorityName);

}
