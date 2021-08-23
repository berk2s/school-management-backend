package com.schoolplus.office.services.impl;

import com.schoolplus.office.domain.Parent;
import com.schoolplus.office.domain.Student;
import com.schoolplus.office.repository.UserRepository;
import com.schoolplus.office.services.ParentService;
import com.schoolplus.office.web.exceptions.ParentNotFoundException;
import com.schoolplus.office.web.exceptions.StudentNotFoundException;
import com.schoolplus.office.web.mappers.ParentMapper;
import com.schoolplus.office.web.models.CreatingParentDto;
import com.schoolplus.office.web.models.EditingParentDto;
import com.schoolplus.office.web.models.ErrorDesc;
import com.schoolplus.office.web.models.ParentDto;
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
public class ParentServiceImpl implements ParentService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ParentMapper parentMapper;

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:users:parents') || hasAuthority('read:parent'))")
    @Override
    public ParentDto getParent(UUID parentId) {
        Parent parent = (Parent) userRepository.findById(parentId)
                .orElseThrow(() -> {
                   log.warn("Parent with given id does not exists [parentId: {}]", parentId.toString());
                   throw new ParentNotFoundException(ErrorDesc.PARENT_NOT_FOUND.getDesc());
                });

        return parentMapper.parentToParentDto(parent);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:users:parents') || hasAuthority('create:parent'))")
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
        });

        log.info("Parent has been created [parentId: {}, performedBy: {}]", savedParent.getId().toString(),
                SecurityContextHolder.getContext().getAuthentication().getName());

        return parentMapper.parentToParentDto(savedParent);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:users:parents') || hasAuthority('edit:parent'))")
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
            });
        }

    }

}
