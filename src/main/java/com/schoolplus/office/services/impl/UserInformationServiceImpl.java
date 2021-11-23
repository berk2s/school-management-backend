package com.schoolplus.office.services.impl;

import com.schoolplus.office.annotations.UpdatingEntity;
import com.schoolplus.office.domain.User;
import com.schoolplus.office.repository.UserRepository;
import com.schoolplus.office.services.UserInformationService;
import com.schoolplus.office.web.exceptions.ChangingPasswordException;
import com.schoolplus.office.web.exceptions.UserNotFoundException;
import com.schoolplus.office.web.models.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserInformationServiceImpl implements UserInformationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @UpdatingEntity(domain = TransactionDomain.USER, action = DomainAction.UPDATE_USER, idArg = "userId")
    @PreAuthorize("hasRole('ROLE_USER') || hasAuthority('profile:manage')")
    @Override
    public void editProfile(UUID userId, EditingUserInformationDto editingUserInformationDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User with given id does not exists [userId: {}]", userId);
                    throw new UserNotFoundException(ErrorDesc.USER_NOT_FOUND.getDesc());
                });

        if (editingUserInformationDto.getFirstName() != null
                && editingUserInformationDto.getFirstName().trim().length() > 0) {
            user.setFirstName(editingUserInformationDto.getFirstName());
        }

        if (editingUserInformationDto.getLastName() != null
                && editingUserInformationDto.getLastName().trim().length() > 0) {
            user.setLastName(editingUserInformationDto.getLastName());
        }

        if (editingUserInformationDto.getEmail() != null
                && editingUserInformationDto.getEmail().trim().length() > 0) {
            user.setEmail(editingUserInformationDto.getEmail());
        }


        if (editingUserInformationDto.getPhoneNumber() != null
                && editingUserInformationDto.getPhoneNumber().trim().length() > 0) {
            user.setPhoneNumber(editingUserInformationDto.getPhoneNumber());
        }

        if (editingUserInformationDto.getUsername() != null
                && editingUserInformationDto.getUsername().trim().length() > 0) {
            user.setUsername(editingUserInformationDto.getUsername());
        }

        userRepository.save(user);

        log.info("User has been updated yourself successfully [userId: {}]", userId);
    }

    @UpdatingEntity(domain = TransactionDomain.USER, action = DomainAction.UPDATE_PASSWORD, idArg = "userId")
    @PreAuthorize("hasRole('ROLE_USER') || hasAuthority('profile:manage')")
    @Override
    public void updatePassword(UUID userId, ChangingPasswordDto changingPasswordDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User with given id does not exists [userId: {}]", userId);
                    throw new UserNotFoundException(ErrorDesc.USER_NOT_FOUND.getDesc());
                });

        if (!passwordEncoder.matches(changingPasswordDto.getCurrentPassword(), user.getPassword())) {
            log.warn("Passwords are not matching [userId: {}]", userId);
            throw new ChangingPasswordException(ErrorDesc.PASSWORDS_ARE_NOT_MATCHING.getDesc());
        }

        if (!changingPasswordDto.getNewPassword().trim().equals(changingPasswordDto.getNewPasswordConfirm())) {
            log.warn("New Passwords are not matching [userId: {}]", userId);
            throw new ChangingPasswordException(ErrorDesc.NEW_PASSWORDS_ARE_NOT_MATCHING.getDesc());
        }

        user.setPassword(passwordEncoder.encode(changingPasswordDto.getNewPassword()));

        userRepository.save(user);

        log.info("User's password has been updated successfully [userId: {}, performedBy: {}]", userId,
                SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
