package com.schoolplus.office.services.impl;

import com.schoolplus.office.annotations.UpdatingEntity;
import com.schoolplus.office.domain.User;
import com.schoolplus.office.repository.UserRepository;
import com.schoolplus.office.services.UserInformationService;
import com.schoolplus.office.web.exceptions.UserNotFoundException;
import com.schoolplus.office.web.models.DomainAction;
import com.schoolplus.office.web.models.EditingUserInformationDto;
import com.schoolplus.office.web.models.ErrorDesc;
import com.schoolplus.office.web.models.TransactionDomain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserInformationServiceImpl implements UserInformationService {

    private final UserRepository userRepository;

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
}
