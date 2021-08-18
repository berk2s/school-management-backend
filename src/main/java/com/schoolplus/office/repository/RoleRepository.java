package com.schoolplus.office.repository;

import com.schoolplus.office.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
