package com.schoolplus.office.services.impl;

import com.schoolplus.office.annotations.ReadingEntity;
import com.schoolplus.office.domain.User;
import com.schoolplus.office.repository.UserRepository;
import com.schoolplus.office.services.UserInfoService;
import com.schoolplus.office.web.exceptions.UserNotFoundException;
import com.schoolplus.office.web.mappers.UserMapper;
import com.schoolplus.office.web.models.DomainAction;
import com.schoolplus.office.web.models.ErrorDesc;
import com.schoolplus.office.web.models.TransactionDomain;
import com.schoolplus.office.web.models.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserInfoServiceImpl implements UserInfoService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @ReadingEntity(domain = TransactionDomain.USERINFO, action = DomainAction.READ_USERINFO)
    @PreAuthorize("hasRole('ROLE_USER') && (hasAuthority('profile:manage') || hasAuthority('read:userinfo'))")
    @Override
    public UserDto getUserInfo(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User with given id does not exists [userId: {}]", userId);
                    throw new UserNotFoundException(ErrorDesc.USER_NOT_FOUND.getDesc());
                });

        return userMapper.userToUserDto(user);
    }
}
