package com.schoolplus.office.services.impl;

import com.schoolplus.office.annotations.CreatingEntity;
import com.schoolplus.office.annotations.DeletingEntity;
import com.schoolplus.office.annotations.ReadingEntity;
import com.schoolplus.office.annotations.UpdatingEntity;
import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.domain.Parent;
import com.schoolplus.office.domain.Student;
import com.schoolplus.office.repository.OrganizationRepository;
import com.schoolplus.office.repository.ParentRepository;
import com.schoolplus.office.repository.UserRepository;
import com.schoolplus.office.services.ParentService;
import com.schoolplus.office.web.exceptions.OrganizationNotFoundException;
import com.schoolplus.office.web.exceptions.ParentNotFoundException;
import com.schoolplus.office.web.exceptions.StudentNotFoundException;
import com.schoolplus.office.web.mappers.ParentMapper;
import com.schoolplus.office.web.models.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ParentServiceImpl implements ParentService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final ParentMapper parentMapper;
    private final ParentRepository parentRepository;

    @ReadingEntity(domain = TransactionDomain.PARENT, action = DomainAction.READ_PARENTS, isList = true)
    @PreAuthorize("hasRole('ROLE_ADMIN') || (hasAuthority('manage:users:parents') || hasAuthority('read:parent'))")
    @Override
    public Page<ParentDto> getParentsByOrganization(Long organizationId, Pageable pageable, String search) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists");
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        Page<Parent> parents;

        if (StringUtils.isEmpty(search) || search.trim().equals("")) {
            parents = parentRepository.findAllByOrganization(organization, pageable);
        } else {
            parents = parentRepository.findAllByOrganizationAndSearchKey(organization, search, pageable);
        }

        return new PageImpl<>(parentMapper.parentToParentDtoForListing(parents.getContent()),
                pageable,
                parents.getTotalElements());
    }

    @ReadingEntity(domain = TransactionDomain.PARENT, action = DomainAction.READ_PARENT)
    @PreAuthorize("hasRole('ROLE_ADMIN') || (hasAuthority('manage:users:parents') || hasAuthority('read:parent'))")
    @Override
    public ParentDto getParent(UUID parentId) {
        Parent parent = (Parent) userRepository.findById(parentId)
                .orElseThrow(() -> {
                    log.warn("Parent with given id does not exists [parentId: {}]", parentId.toString());
                    throw new ParentNotFoundException(ErrorDesc.PARENT_NOT_FOUND.getDesc());
                });

        return parentMapper.parentToParentDto(parent);
    }

    @CreatingEntity(domain = TransactionDomain.PARENT, action = DomainAction.CREATE_PARENT)
    @PreAuthorize("hasRole('ROLE_ADMIN') || (hasAuthority('manage:users:parents') || hasAuthority('create:parent'))")
    @Override
    public ParentDto createParent(CreatingParentDto creatingParent) {
        Parent parent = new Parent();
        parent.setUsername(creatingParent.getUsername());
        parent.setPassword(passwordEncoder.encode(creatingParent.getPassword()));
        parent.setFirstName(creatingParent.getFirstName());
        parent.setLastName(creatingParent.getLastName());
        parent.setPhoneNumber(creatingParent.getPhoneNumber());
        parent.setEmail(creatingParent.getEmail());
        parent.setIsEnabled(creatingParent.getIsEnabled());
        parent.setIsAccountNonLocked(creatingParent.getIsAccountNonLocked());
        parent.setIsAccountNonExpired(creatingParent.getIsAccountNonExpired());
        parent.setIsCredentialsNonExpired(creatingParent.getIsCredentialsNonExpired());

        Organization organization = organizationRepository.findById(creatingParent.getOrganizationId())
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists");
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        parent.setOrganization(organization);

        Parent savedParent = userRepository.save(parent);

        creatingParent.getStudents().forEach(_studentId -> {
            UUID studentId = UUID.fromString(_studentId);

            Student student = (Student) userRepository.findById(studentId)
                    .orElseThrow(() -> {
                        log.warn("Student with given id does not exists [studentId: {}]", studentId.toString());
                        throw new StudentNotFoundException(ErrorDesc.STUDENT_NOT_FOUND.getDesc());
                    });

            if (!student.getParents().contains(savedParent)) {
                student.addParent(savedParent);
            }

            userRepository.save(student);
        });

        log.info("Parent has been created [parentId: {}, performedBy: {}]", savedParent.getId().toString(),
                SecurityContextHolder.getContext().getAuthentication().getName());

        return parentMapper.parentToParentDto(savedParent);
    }

    @UpdatingEntity(domain = TransactionDomain.PARENT, action = DomainAction.UPDATE_PARENT, idArg = "parentId")
    @PreAuthorize("hasRole('ROLE_ADMIN') || (hasAuthority('manage:users:parents') || hasAuthority('edit:parent'))")
    @Override
    public void updateParent(UUID parentId, EditingParentDto editingParent) {
        Parent parent = (Parent) userRepository.findById(parentId)
                .orElseThrow(() -> {
                    log.warn("Parent with given id does not exists [parentId: {}]", parentId.toString());
                    throw new ParentNotFoundException(ErrorDesc.PARENT_NOT_FOUND.getDesc());
                });

        if (editingParent.getAddedStudents() != null && editingParent.getAddedStudents().size() != 0) {
            editingParent.getAddedStudents().forEach(_studentId -> {
                UUID studentId = UUID.fromString(_studentId);

                Student student = (Student) userRepository.findById(studentId)
                        .orElseThrow(() -> {
                            log.warn("Student with given id does not exists [studentId: {}]", studentId.toString());
                            throw new StudentNotFoundException(ErrorDesc.STUDENT_NOT_FOUND.getDesc());
                        });

                student.addParent(parent);

                userRepository.save(student);
            });
        }

        if (editingParent.getDeletedStudents() != null && editingParent.getDeletedStudents().size() != 0) {
            editingParent.getDeletedStudents().forEach(_studentId -> {
                UUID studentId = UUID.fromString(_studentId);

                Student student = (Student) userRepository.findById(studentId)
                        .orElseThrow(() -> {
                            log.warn("Student with given id does not exists [studentId: {}]", studentId.toString());
                            throw new StudentNotFoundException(ErrorDesc.STUDENT_NOT_FOUND.getDesc());
                        });

                student.removeParent(parent);

                userRepository.save(student);
            });
        }

        userRepository.save(parent);

        log.info("Parent has been edited [parentId: {}, performedBy: {}]", parent.getId().toString(),
                SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Transactional
    @DeletingEntity(domain = TransactionDomain.PARENT, action = DomainAction.DELETE_PARENT, idArg = "parentId")
    @PreAuthorize("hasRole('ROLE_ADMIN') || (hasAuthority('manage:users:parents') || hasAuthority('delete:parent'))")
    @Override
    public void deleteParent(UUID parentId) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> {
                    log.warn("Parent with given id does not exists [parentId: {}]", parentId.toString());
                    throw new ParentNotFoundException(ErrorDesc.PARENT_NOT_FOUND.getDesc());
                });

        if (parent.getStudents() != null) {
            parent.getStudents().forEach(student -> {
                student.getParents().remove(parent);
            });
        }

        parentRepository.deleteById(parentId);

        log.info("Parent has been deleted successfully [parentId: {}, performedBy: {}]", parent.getId().toString(),
                SecurityContextHolder.getContext().getAuthentication().getName());
    }

}
