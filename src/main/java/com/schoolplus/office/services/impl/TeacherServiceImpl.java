package com.schoolplus.office.services.impl;

import com.schoolplus.office.annotations.CreatingEntity;
import com.schoolplus.office.annotations.ReadingEntity;
import com.schoolplus.office.annotations.UpdatingEntity;
import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.domain.Teacher;
import com.schoolplus.office.domain.TeachingSubject;
import com.schoolplus.office.repository.OrganizationRepository;
import com.schoolplus.office.repository.TeachingSubjectRepository;
import com.schoolplus.office.repository.UserRepository;
import com.schoolplus.office.services.TeacherService;
import com.schoolplus.office.web.exceptions.OrganizationNotFoundException;
import com.schoolplus.office.web.exceptions.TeacherNotFoundException;
import com.schoolplus.office.web.exceptions.TeachingSubjectNotFoundException;
import com.schoolplus.office.web.mappers.TeacherMapper;
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
public class TeacherServiceImpl implements TeacherService {

    private final UserRepository userRepository;
    private final TeachingSubjectRepository teachingSubjectRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final TeacherMapper teacherMapper;

    @ReadingEntity(domain = TransactionDomain.TEACHER, action = DomainAction.READ_TEACHER)
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:users:teachers') || hasAuthority('read:teacher'))")
    @Override
    public TeacherDto getTeacher(UUID teacherId) {
        Teacher teacher = (Teacher) userRepository.findById(teacherId)
                .orElseThrow(() -> {
                   log.warn("Teacher with given id does not exists [teacherId: {}]", teacherId);
                    throw new TeacherNotFoundException(ErrorDesc.TEACHER_NOT_FOUND.getDesc());
                });

        return teacherMapper.teacherToTeacherDto(teacher);
    }

    @CreatingEntity(domain = TransactionDomain.TEACHER, action = DomainAction.CREATE_TEACHER)
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:users:teachers') || hasAuthority('create:teacher'))")
    @Override
    public TeacherDto createTeacher(CreatingTeacherDto creatingTeacher) {
        Teacher teacher = new Teacher();
        teacher.setUsername(creatingTeacher.getUsername());
        teacher.setPassword(passwordEncoder.encode(creatingTeacher.getPassword()));
        teacher.setFirstName(creatingTeacher.getFirstName());
        teacher.setLastName(creatingTeacher.getLastName());
        teacher.setPhoneNumber(creatingTeacher.getPhoneNumber());
        teacher.setEmail(creatingTeacher.getEmail());
        teacher.setIsEnabled(creatingTeacher.getIsEnabled());
        teacher.setIsAccountNonLocked(creatingTeacher.getIsAccountNonLocked());
        teacher.setIsAccountNonExpired(creatingTeacher.getIsAccountNonExpired());
        teacher.setIsCredentialsNonExpired(creatingTeacher.getIsCredentialsNonExpired());

        Organization organization = organizationRepository.findById(creatingTeacher.getOrganizationId())
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists");
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        teacher.setOrganization(organization);

        if(creatingTeacher.getTeachingSubjects() != null && creatingTeacher.getTeachingSubjects().size() != 0) {
            creatingTeacher.getTeachingSubjects().forEach(teachingSubjectId -> {
                TeachingSubject teachingSubject = teachingSubjectRepository.findById(teachingSubjectId)
                        .orElseThrow(() -> {
                            log.warn("Teaching Subject with given id does not exists [teachingSubjectId: {}]", teachingSubjectId);
                            throw new TeachingSubjectNotFoundException(ErrorDesc.TEACHING_SUBJECT_NOT_FOUND.getDesc());
                        });

                teacher.addTeachingSubject(teachingSubject);
            });
        }

        Teacher savedTeacher = userRepository.save(teacher);

        log.info("Teacher has been created [teacherId: {}, performedBy: {}]", teacher.getId().toString(),
                SecurityContextHolder.getContext().getAuthentication().getName());

        return teacherMapper.teacherToTeacherDto(savedTeacher);
    }

    @UpdatingEntity(domain = TransactionDomain.TEACHER, action = DomainAction.UPDATE_TEACHER, idArg = "teacherId")
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:users:teachers') || hasAuthority('create:teacher'))")
    @Override
    public void updateTeacher(UUID teacherId, EditingTeacherDto editTeacher) {
        Teacher teacher = (Teacher) userRepository.findById(teacherId)
                .orElseThrow(() -> {
                    log.warn("Teacher with given id does not exists [teacherId: {}]", teacherId);
                    throw new TeacherNotFoundException(ErrorDesc.TEACHER_NOT_FOUND.getDesc());
                });

        if (editTeacher.getAddedTeachingSubjects() != null && editTeacher.getAddedTeachingSubjects().size() != 0) {
            editTeacher.getAddedTeachingSubjects().forEach(teachingSubjectId -> {
                TeachingSubject teachingSubject = teachingSubjectRepository.findById(teachingSubjectId)
                        .orElseThrow(() -> {
                            log.warn("Teaching Subject with given id does not exists [teachingSubjectId: {}]", teachingSubjectId.toString());
                            throw new TeachingSubjectNotFoundException(ErrorDesc.TEACHING_SUBJECT_NOT_FOUND.getDesc());
                        });

                teacher.addTeachingSubject(teachingSubject);
            });
        }

        if (editTeacher.getRemoveTeachingSubjects() != null && editTeacher.getRemoveTeachingSubjects().size() != 0) {
            editTeacher.getRemoveTeachingSubjects().forEach(teachingSubjectId -> {
                TeachingSubject teachingSubject = teachingSubjectRepository.findById(teachingSubjectId)
                        .orElseThrow(() -> {
                            log.warn("Teaching Subject with given id does not exists [teachingSubjectId: {}]", teachingSubjectId.toString());
                            throw new TeachingSubjectNotFoundException(ErrorDesc.TEACHING_SUBJECT_NOT_FOUND.getDesc());
                        });

                teacher.removeTeachingSubject(teachingSubject);
            });
        }

        log.info("Teacher has been updated [teacherId: {}, performedBy: {}]", teacher.getId().toString(),
                SecurityContextHolder.getContext().getAuthentication().getName());

        userRepository.save(teacher);
    }


}
