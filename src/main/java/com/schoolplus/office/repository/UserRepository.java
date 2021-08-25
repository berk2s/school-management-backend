package com.schoolplus.office.repository;

import com.schoolplus.office.domain.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends PagingAndSortingRepository<User, UUID> {

    Optional<User> findByUsername(String username);

}
