package com.schoolplus.office.services.impl;

import com.schoolplus.office.annotations.DeletingEntity;
import com.schoolplus.office.annotations.ReadingEntity;
import com.schoolplus.office.annotations.UpdatingEntity;
import com.schoolplus.office.domain.Authority;
import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.domain.Role;
import com.schoolplus.office.domain.User;
import com.schoolplus.office.repository.AuthorityRepository;
import com.schoolplus.office.repository.OrganizationRepository;
import com.schoolplus.office.repository.RoleRepository;
import com.schoolplus.office.repository.UserRepository;
import com.schoolplus.office.services.UserService;
import com.schoolplus.office.web.exceptions.AuthorityNotFoundException;
import com.schoolplus.office.web.exceptions.OrganizationNotFoundException;
import com.schoolplus.office.web.exceptions.RoleNotFoundException;
import com.schoolplus.office.web.exceptions.UserNotFoundException;
import com.schoolplus.office.web.mappers.UserMapper;
import com.schoolplus.office.web.models.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final OrganizationRepository organizationRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @ReadingEntity(domain = TransactionDomain.USER, action = DomainAction.READ_USER)
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:users') || hasAuthority('read:user'))")
    @Override
    public UserDto getUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User with given id does not exists given [userId: {}]", userId);
                    throw new UserNotFoundException(ErrorDesc.USER_NOT_FOUND.getDesc());
                });

        return userMapper.userToUserDto(user);
    }

    @ReadingEntity(domain = TransactionDomain.USER, action = DomainAction.READ_USERS, isList = true)
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:users') || hasAuthority('list:users'))")
    @Override
    public List<UserDto> listUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return userMapper.userToUserDto(users.getContent());
    }

    @UpdatingEntity(domain = TransactionDomain.USER, action = DomainAction.UPDATE_ANNOUNCEMENT, idArg = "userId")
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:users') || hasAuthority('edit:user'))")
    @Override
    public void editUser(UUID userId, EditingUserDto editUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User with given id does not exists given [userId: {}]", userId);
                    throw new UserNotFoundException(ErrorDesc.USER_NOT_FOUND.getDesc());
                });

        if (editUser.getUsername() != null)
            user.setUsername(editUser.getUsername());

        if (editUser.getPassword() != null)
            user.setPassword(passwordEncoder.encode(editUser.getPassword()));

        if (editUser.getIsAccountNonExpired() != null)
            user.setIsAccountNonExpired(editUser.getIsAccountNonExpired());

        if (editUser.getIsAccountNonLocked() != null)
            user.setIsAccountNonLocked(editUser.getIsAccountNonLocked());

        if (editUser.getIsCredentialsNonExpired() != null)
            user.setIsAccountNonLocked(editUser.getIsAccountNonLocked());

        if (editUser.getIsEnabled() != null)
            user.setIsEnabled(editUser.getIsEnabled());

        if (editUser.getOrganizationId() != null && !editUser.getOrganizationId().equals(user.getOrganization().getId())) {
            Organization organization = organizationRepository.findById(editUser.getOrganizationId())
                    .orElseThrow(() -> {
                        log.warn("Organization with given id does not exists");
                        throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                    });

            user.setOrganization(organization);
        }

        if (editUser.getDeletedAuthorities() != null && editUser.getNewAuthorities().size() != 0)
            addNewAuthority(editUser, user);

        if (editUser.getDeletedAuthorities() != null && editUser.getDeletedAuthorities().size() != 0)
            deleteGivenAuthority(editUser, user);

        if (editUser.getNewRoles() != null && editUser.getNewRoles().size() != 0)
            addNewRole(editUser, user);

        if (editUser.getDeletedRoles() != null && editUser.getDeletedRoles().size() != 0)
            deleteGivenRole(editUser, user);

        log.info("User has been edited successfully [userId: {}, performedBy: {}]", userId,
                SecurityContextHolder.getContext().getAuthentication().getName());

        userRepository.save(user);
    }

    @DeletingEntity(domain = TransactionDomain.USER, action = DomainAction.DELETE_USER, idArg = "userId")
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:users') || hasAuthority('delete:user'))")
    @Override
    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            log.warn("User with given id does not exists given [userId: {}]", userId);
            throw new UserNotFoundException(ErrorDesc.USER_NOT_FOUND.getDesc());
        }

        log.info("User has been deleted successfully [userId: {}, performedBy: {}]", userId,
                SecurityContextHolder.getContext().getAuthentication().getName());

        userRepository.deleteById(userId);
    }

    private void deleteGivenRole(EditingUserDto editUser, User user) {
        editUser.getDeletedRoles().forEach(_deletedRoleId -> {
            Long deletedRoleId = Long.valueOf(_deletedRoleId);

            boolean hasUserThatRole = user.getRoles().stream()
                    .map(Role::getId)
                    .anyMatch(roleId -> Objects.equals(roleId, deletedRoleId));

            if (hasUserThatRole) {
                Role role = roleRepository
                        .findById(deletedRoleId)
                        .orElseThrow(() -> {
                            log.warn("Role with given id does not exists [roleId: {}]", deletedRoleId);
                            throw new RoleNotFoundException(ErrorDesc.ROLE_NOT_FOUND.getDesc());
                        });

                user.deleteRole(role);
            } else {
                log.warn("Role with given id does not exists [roleId: {}]", deletedRoleId);
                throw new RoleNotFoundException(ErrorDesc.ROLE_NOT_FOUND.getDesc());
            }
        });
    }

    private void addNewRole(EditingUserDto editUser, User user) {
        editUser.getNewRoles().forEach(_givenRoleId -> {
            Long givenRoleId = Long.valueOf(_givenRoleId);

            boolean isNew = user.getRoles().stream()
                    .map(Role::getId)
                    .noneMatch(roleId -> Objects.equals(roleId, givenRoleId));

            if (isNew) {
                Role role = roleRepository
                        .findById(givenRoleId)
                        .orElseThrow(() -> {
                            log.warn("Role with given id does not exists [roleId: {}]", givenRoleId);
                            throw new RoleNotFoundException(ErrorDesc.ROLE_NOT_FOUND.getDesc());
                        });

                user.addRole(role);
            } else {
                log.warn("Role with given id does not exists [roleId: {}]", givenRoleId);
                throw new RoleNotFoundException(ErrorDesc.ROLE_NOT_FOUND.getDesc());
            }
        });
    }

    private void deleteGivenAuthority(EditingUserDto editUser, User user) {
        editUser.getDeletedAuthorities().forEach(_deletedAuthrotiyId -> {
            Long deletedAuthorityId = Long.valueOf(_deletedAuthrotiyId);

            boolean hasUserThatAuthority = user.getAuthorities().stream()
                    .map(Authority::getId)
                    .anyMatch(authorityId -> Objects.equals(authorityId, deletedAuthorityId));

            if (hasUserThatAuthority) {
                Authority authority = authorityRepository
                        .findById(deletedAuthorityId)
                        .orElseThrow(() -> {
                            log.warn("Authority with given id does not exists [authorityId: {}]", deletedAuthorityId);
                            throw new AuthorityNotFoundException(ErrorDesc.AUTHORITY_NOT_FOUND.getDesc());
                        });

                user.deleteAuthority(authority);
            } else {
                log.warn("Authority with given id does not exists [authorityId: {}]", deletedAuthorityId);
                throw new AuthorityNotFoundException(ErrorDesc.AUTHORITY_NOT_FOUND.getDesc());
            }
        });
    }

    private void addNewAuthority(EditingUserDto editUser, User user) {
        editUser.getNewAuthorities().forEach(_givenAuthorityId -> {
            Long givenAuthorityId = Long.valueOf(_givenAuthorityId);

            boolean isNew = user.getAuthorities().stream()
                    .map(Authority::getId)
                    .noneMatch(authorityId -> Objects.equals(authorityId, givenAuthorityId));

            if (isNew) {
                Authority authority = authorityRepository
                        .findById(givenAuthorityId)
                        .orElseThrow(() -> {
                            log.warn("Authority with given id does not exists [authorityId: {}]", givenAuthorityId);
                            throw new AuthorityNotFoundException(ErrorDesc.AUTHORITY_NOT_FOUND.getDesc());
                        });

                user.addAuthority(authority);
            } else {
                log.warn("Authority with given id does not exists [authorityId: {}]", givenAuthorityId);
                throw new AuthorityNotFoundException(ErrorDesc.AUTHORITY_NOT_FOUND.getDesc());
            }
        });
    }
}
