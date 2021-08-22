package com.schoolplus.office.services.impl;

import com.schoolplus.office.domain.Authority;
import com.schoolplus.office.domain.Role;
import com.schoolplus.office.domain.Student;
import com.schoolplus.office.repository.AuthorityRepository;
import com.schoolplus.office.repository.RoleRepository;
import com.schoolplus.office.repository.UserRepository;
import com.schoolplus.office.services.StudentService;
import com.schoolplus.office.web.exceptions.AuthorityNotFoundException;
import com.schoolplus.office.web.exceptions.RoleNotFoundException;
import com.schoolplus.office.web.mappers.StudentMapper;
import com.schoolplus.office.web.models.CreatingStudentDto;
import com.schoolplus.office.web.models.ErrorDesc;
import com.schoolplus.office.web.models.StudentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class StudentServiceImpl implements StudentService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final RoleRepository roleRepository;
    private final StudentMapper studentMapper;
    private final PasswordEncoder passwordEncoder;

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

        creatingStudent.getRoles().forEach(roleId -> {
            Role role = roleRepository
                    .findById(roleId)
                    .orElseThrow(() -> {
                        log.warn("Role with given id does not exists [roleId: {}]", roleId);
                        throw new RoleNotFoundException(ErrorDesc.ROLE_NOT_FOUND.getDesc());
                    });

            student.addRole(role);
        });

        creatingStudent.getAuthorities().forEach(authorityId -> {
            Authority authority = authorityRepository
                    .findById(authorityId)
                    .orElseThrow(() -> {
                        log.warn("Authority with given id does not exists [authorityId: {}]", authorityId);
                        throw new AuthorityNotFoundException(ErrorDesc.AUTHORITY_NOT_FOUND.getDesc());
                    });

            student.addAuthority(authority);
        });

        Student savedStudent = userRepository.save(student);

        return studentMapper.studentToStudentDto(savedStudent);
    }
}
