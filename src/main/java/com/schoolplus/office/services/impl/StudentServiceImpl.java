package com.schoolplus.office.services.impl;

import com.schoolplus.office.domain.*;
import com.schoolplus.office.repository.*;
import com.schoolplus.office.services.StudentService;
import com.schoolplus.office.web.exceptions.*;
import com.schoolplus.office.web.mappers.StudentMapper;
import com.schoolplus.office.web.models.CreatingStudentDto;
import com.schoolplus.office.web.models.EditingStudentDto;
import com.schoolplus.office.web.models.ErrorDesc;
import com.schoolplus.office.web.models.StudentDto;
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
public class StudentServiceImpl implements StudentService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final OrganizationRepository organizationRepository;
    private final RoleRepository roleRepository;
    private final GradeRepository gradeRepository;
    private final StudentMapper studentMapper;
    private final PasswordEncoder passwordEncoder;

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:users:students') || hasAuthority('read:student'))")
    @Override
    public StudentDto getStudent(UUID studentId) {
        Student student = (Student) userRepository.findById(studentId)
                .orElseThrow(() -> {
                    log.warn("Student with given id does not exists [studentId: {}]", studentId.toString());
                    throw new StudentNotFoundException(ErrorDesc.STUDENT_NOT_FOUND.getDesc());
                });

        return studentMapper.studentToStudentDto(student);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:users:students') || hasAuthority('create:student'))")
    @Override
    public StudentDto createStudent(CreatingStudentDto creatingStudent) {
        Student student = new Student();
        student.setUsername(creatingStudent.getUsername());
        student.setPassword(passwordEncoder.encode(creatingStudent.getPassword()));
        student.setFirstName(creatingStudent.getFirstName());
        student.setLastName(creatingStudent.getLastName());
        student.setPhoneNumber(creatingStudent.getPhoneNumber());
        student.setEmail(creatingStudent.getEmail());
        student.setIsEnabled(creatingStudent.getIsEnabled());
        student.setIsAccountNonLocked(creatingStudent.getIsAccountNonLocked());
        student.setIsAccountNonExpired(creatingStudent.getIsAccountNonExpired());
        student.setIsCredentialsNonExpired(creatingStudent.getIsCredentialsNonExpired());
        student.setGradeType(creatingStudent.getGradeType());
        student.setGradeLevel(creatingStudent.getGradeLevel());

        Organization organization = organizationRepository.findById(creatingStudent.getOrganizationId())
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists");
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        student.setOrganization(organization);

        if (creatingStudent.getGradeId() != null) {
            Long gradeId = creatingStudent.getGradeId();
            Grade grade = gradeRepository.findById(gradeId)
                    .orElseThrow(() -> {
                       log.warn("Grade with given id does not exists [gradeId: {}]", gradeId);
                       throw new GradeNotFoundException(ErrorDesc.GRADE_NOT_FOUND.getDesc());
                    });

            grade.addStudent(student);
        }

        if(creatingStudent.getParents() != null && creatingStudent.getParents().size() > 0) {
            creatingStudent.getParents().forEach(_parentId -> {
                UUID parentId = UUID.fromString(_parentId);

                Parent parent = (Parent) userRepository.findById(parentId)
                        .orElseThrow(() -> {
                            log.warn("Parent with given id does not exists [parentId: {}]", parentId.toString());
                            throw new ParentNotFoundException(ErrorDesc.PARENT_NOT_FOUND.getDesc());
                        });

                student.addParent(parent);
            });
        }

        if (creatingStudent.getRoles() != null && creatingStudent.getRoles().size() > 0) {
            creatingStudent.getRoles().forEach(roleId -> {
                Role role = roleRepository
                        .findById(roleId)
                        .orElseThrow(() -> {
                            log.warn("Role with given id does not exists [roleId: {}]", roleId);
                            throw new RoleNotFoundException(ErrorDesc.ROLE_NOT_FOUND.getDesc());
                        });

                student.addRole(role);
            });
        }

        if (creatingStudent.getAuthorities() != null && creatingStudent.getAuthorities().size() > 0) {
            creatingStudent.getAuthorities().forEach(authorityId -> {
                Authority authority = authorityRepository
                        .findById(authorityId)
                        .orElseThrow(() -> {
                            log.warn("Authority with given id does not exists [authorityId: {}]", authorityId);
                            throw new AuthorityNotFoundException(ErrorDesc.AUTHORITY_NOT_FOUND.getDesc());
                        });

                student.addAuthority(authority);
            });
        }

        Student savedStudent = userRepository.save(student);

        log.info("User has been created [userId: {}, performedBy: {}]", savedStudent.getId().toString(),
                SecurityContextHolder.getContext().getAuthentication().getName());

        return studentMapper.studentToStudentDto(savedStudent);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:users:students') || hasAuthority('edit:student'))")
    @Override
    public void editStudent(UUID studentId, EditingStudentDto editStudent) {
        Student student = (Student) userRepository.findById(studentId)
                        .orElseThrow(() -> {
                            log.warn("Student with given id does not exists [studentId: {}]", studentId.toString());
                            throw new StudentNotFoundException(ErrorDesc.STUDENT_NOT_FOUND.getDesc());
                        });

        if (editStudent.getGradeLevel() != null)
            student.setGradeLevel(editStudent.getGradeLevel());

        if (editStudent.getGradeType() != null)
            student.setGradeType(editStudent.getGradeType());

        if (editStudent.getAddedParents() != null && editStudent.getAddedParents().size() != 0) {
            editStudent.getAddedParents().forEach(_parentId -> {
                UUID parentId = UUID.fromString(_parentId);

                Parent parent = (Parent) userRepository.findById(parentId)
                        .orElseThrow(() -> {
                            log.warn("Parent with given id does not exists [parentId: {}]", parentId.toString());
                            throw new ParentNotFoundException(ErrorDesc.PARENT_NOT_FOUND.getDesc());
                        });

                student.addParent(parent);

            });
        }

        if (editStudent.getDeletedParents() != null && editStudent.getDeletedParents().size() != 0) {
            editStudent.getDeletedParents().forEach(_parentId -> {
                UUID parentId = UUID.fromString(_parentId);

                Parent parent = (Parent) userRepository.findById(parentId)
                        .orElseThrow(() -> {
                            log.warn("Parent with given id does not exists [parentId: {}]", parentId.toString());
                            throw new ParentNotFoundException(ErrorDesc.PARENT_NOT_FOUND.getDesc());
                        });

                student.removeParent(parent);

            });
        }

        if (editStudent.getGradeId() != null) {
            Long gradeId = editStudent.getGradeId();
            Grade grade = gradeRepository.findById(gradeId)
                    .orElseThrow(() -> {
                        log.warn("Grade with given id does not exists [gradeId: {}]", gradeId);
                        throw new GradeNotFoundException(ErrorDesc.GRADE_NOT_FOUND.getDesc());
                    });

            grade.addStudent(student);
        }


        log.info("User has been edited [userId: {}, performedBy: {}]", student.getId().toString(),
                SecurityContextHolder.getContext().getAuthentication().getName());

        userRepository.save(student);
    }
}
