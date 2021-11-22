package com.schoolplus.office.services.impl;

import com.schoolplus.office.repository.UserRepository;
import com.schoolplus.office.services.ValidationService;
import com.schoolplus.office.web.models.ValidationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ValidationServiceImpl implements ValidationService {

    private final UserRepository userRepository;

    @PreAuthorize("hasRole('ROLE_USER')")
    @Override
    public ValidationDto validateUsername(String givenData) {
        return ValidationDto.builder()
                .isTaken(userRepository.existsByUsername(givenData))
                .build();
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Override
    public ValidationDto validatePhoneNumber(String givenData) {
        return ValidationDto.builder()
                .isTaken(userRepository.existsByPhoneNumber(givenData))
                .build();
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Override
    public ValidationDto validateEmail(String givenData) {
        return ValidationDto.builder()
                .isTaken(userRepository.existsByEmail(givenData))
                .build();
    }
}
