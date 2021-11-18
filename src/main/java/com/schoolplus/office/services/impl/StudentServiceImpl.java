package com.schoolplus.office.services.impl;

import com.schoolplus.office.annotations.CreatingEntity;
import com.schoolplus.office.annotations.ReadingEntity;
import com.schoolplus.office.annotations.UpdatingEntity;
import com.schoolplus.office.domain.*;
import com.schoolplus.office.repository.*;
import com.schoolplus.office.services.StudentService;
import com.schoolplus.office.web.exceptions.*;
import com.schoolplus.office.web.mappers.StudentMapper;
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

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class StudentServiceImpl implements StudentService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final OrganizationRepository organizationRepository;
    private final RoleRepository roleRepository;
    private final ClassroomRepository classroomRepository;
    private final StudentMapper studentMapper;
    private final PasswordEncoder passwordEncoder;

    @ReadingEntity(domain = TransactionDomain.ANNOUNCEMENT, action = DomainAction.READ_ANNOUNCEMENTS, isList = true)
    @PreAuthorize("hasRole('ROLE_ADMIN') || hasAuthority('manage:users:students') || hasAuthority('read:students')")
    @Override
    public Page<StudentDto> getStudentsByOrganization(Long organizationId,
                                                      Pageable pageable,
                                                      String search) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists [organizationId: {}]", organizationId);
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        Page<Student> students;

        if (StringUtils.isEmpty(search) || search.trim().equals("")) {
            students = userRepository.findAllStudentsByOrganization(organization, pageable);
        } else {
            students = userRepository
                    .findAllStudentsByOrganizationAndSearchKey(
                            organization,
                            search.trim(),
                            pageable);
        }

        return new PageImpl<>(
                studentMapper.studentToStudentDto(students.getContent()),
                pageable,
                students.getTotalElements());
    }

    @ReadingEntity(domain = TransactionDomain.STUDENT, action = DomainAction.READ_STUDENT)
    @PreAuthorize("hasRole('ROLE_ADMIN') || (hasAuthority('manage:users:students') || hasAuthority('read:student'))")
    @Override
    public StudentDto getStudent(UUID studentId) {
        Student student = (Student) userRepository.findById(studentId)
                .orElseThrow(() -> {
                    log.warn("Student with given id does not exists [studentId: {}]", studentId.toString());
                    throw new StudentNotFoundException(ErrorDesc.STUDENT_NOT_FOUND.getDesc());
                });

        return studentMapper.studentToStudentDto(student);
    }

    @CreatingEntity(domain = TransactionDomain.STUDENT, action = DomainAction.CREATE_STUDENT)
    @PreAuthorize("hasRole('ROLE_ADMIN') || (hasAuthority('manage:users:students') || hasAuthority('create:student'))")
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

        Organization organization = organizationRepository.findById(creatingStudent.getOrganizationId())
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists");
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        student.setOrganization(organization);

        if (creatingStudent.getClassRoomId() != null) {
            Long gradeId = creatingStudent.getClassRoomId();
            Classroom classRoom = classroomRepository.findById(gradeId)
                    .orElseThrow(() -> {
                        log.warn("Grade with given id does not exists [gradeId: {}]", gradeId);
                        throw new ClassroomNotFoundException(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc());
                    });

            classRoom.addStudent(student);
        }

        if (creatingStudent.getParents() != null && creatingStudent.getParents().size() > 0) {
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

    @UpdatingEntity(domain = TransactionDomain.STUDENT, action = DomainAction.UPDATE_STUDENT, idArg = "studentId")
    @PreAuthorize("hasRole('ROLE_ADMIN') || (hasAuthority('manage:users:students') || hasAuthority('edit:student'))")
    @Override
    public void editStudent(UUID studentId, EditingStudentDto editStudent) {
        Student student = (Student) userRepository.findById(studentId)
                .orElseThrow(() -> {
                    log.warn("Student with given id does not exists [studentId: {}]", studentId.toString());
                    throw new StudentNotFoundException(ErrorDesc.STUDENT_NOT_FOUND.getDesc());
                });

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

        if (editStudent.getClassRoomId() != null) {
            Long gradeId = editStudent.getClassRoomId();
            Classroom classRoom = classroomRepository.findById(gradeId)
                    .orElseThrow(() -> {
                        log.warn("Grade with given id does not exists [gradeId: {}]", gradeId);
                        throw new ClassroomNotFoundException(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc());
                    });

            classRoom.addStudent(student);
        }


        log.info("User has been edited [userId: {}, performedBy: {}]", student.getId().toString(),
                SecurityContextHolder.getContext().getAuthentication().getName());

        userRepository.save(student);
    }
}
