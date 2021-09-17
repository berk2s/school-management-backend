package com.schoolplus.office.services.impl;

import com.schoolplus.office.annotations.CreatingEntity;
import com.schoolplus.office.annotations.DeletingEntity;
import com.schoolplus.office.annotations.ReadingEntity;
import com.schoolplus.office.annotations.UpdatingEntity;
import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.domain.Teacher;
import com.schoolplus.office.domain.TeachingSubject;
import com.schoolplus.office.repository.OrganizationRepository;
import com.schoolplus.office.repository.TeachingSubjectRepository;
import com.schoolplus.office.repository.UserRepository;
import com.schoolplus.office.services.TeachingSubjectService;
import com.schoolplus.office.web.exceptions.OrganizationNotFoundException;
import com.schoolplus.office.web.exceptions.TeacherNotFoundException;
import com.schoolplus.office.web.exceptions.TeachingSubjectNotFoundException;
import com.schoolplus.office.web.mappers.TeachingSubjectMapper;
import com.schoolplus.office.web.models.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class TeachingSubjectServiceImpl implements TeachingSubjectService {

    private final TeachingSubjectRepository teachingSubjectRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final TeachingSubjectMapper teachingSubjectMapper;

    @ReadingEntity(domain = TransactionDomain.TEACHING_SUBJECT, action = DomainAction.READ_TEACHING_SUBJECTS, isList = true)
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:teachingsubjects') || hasAuthority('read:teachingsubject'))")
    @Override
    public List<TeachingSubjectDto> getTeachingSubjects(Pageable pageable) {
        Page<TeachingSubject> teachingSubjects = teachingSubjectRepository
                .findAll(pageable);

        return teachingSubjectMapper.teachingSubjectToTeachingSubjectDto(teachingSubjects.getContent());
    }

    @ReadingEntity(domain = TransactionDomain.TEACHING_SUBJECT, action = DomainAction.READ_TEACHING_SUBJECT)
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:teachingsubjects') || hasAuthority('read:teachingsubject'))")
    @Override
    public TeachingSubjectDto getTeachingSubject(Long teachingSubjectId) {
        TeachingSubject teachingSubject = teachingSubjectRepository.findById(teachingSubjectId)
                .orElseThrow(() -> {
                    log.warn("Teaching Subject with given id does not exists [teachingSubjectId: {}]", teachingSubjectId);
                    throw new TeachingSubjectNotFoundException(ErrorDesc.TEACHING_SUBJECT_NOT_FOUND.getDesc());
                });

        return teachingSubjectMapper.teachingSubjectToTeachingSubjectDto(teachingSubject);
    }

    @CreatingEntity(domain = TransactionDomain.TEACHING_SUBJECT, action = DomainAction.CREATE_TEACHING_SUBJECT)
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:teachingsubjects') || hasAuthority('write:teachingsubject'))")
    @Override
    public TeachingSubjectDto createTeachingSubject(CreatingTeachingSubjectDto creatingTeachingSubject) {
        TeachingSubject teachingSubject = new TeachingSubject();
        teachingSubject.setSubjectName(creatingTeachingSubject.getSubjectName());

        if (creatingTeachingSubject.getTeachers() != null && creatingTeachingSubject.getTeachers().size() > 0) {
            creatingTeachingSubject.getTeachers().forEach(_teacherId -> {
                UUID teacherId = UUID.fromString(_teacherId);

                Teacher teacher = (Teacher) userRepository.findById(teacherId)
                        .orElseThrow(() -> {
                            log.warn("Teacher with given id does not exists [teacherId: {}]", teacherId);
                            throw new TeacherNotFoundException(ErrorDesc.TEACHER_NOT_FOUND.getDesc());
                        });

                teacher.addTeachingSubject(teachingSubject);
            });
        }

        Organization organization = organizationRepository.findById(creatingTeachingSubject.getOrganizationId())
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists [organizationId: {}]", creatingTeachingSubject.getOrganizationId());
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        teachingSubject.setOrganization(organization);

        log.info("The Teaching Subject has been created successfully [teachingSubjectId: {}, performedBy: {}]",
                teachingSubject.getId(), SecurityContextHolder.getContext().getAuthentication().getName());

        return teachingSubjectMapper.teachingSubjectToTeachingSubjectDto(teachingSubjectRepository.save(teachingSubject));
    }

    @UpdatingEntity(domain = TransactionDomain.TEACHING_SUBJECT, action = DomainAction.UPDATE_TEACHING_SUBJECT, idArg = "teachingSubjectId")
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:teachingsubjects') || hasAuthority('update:teachingsubject'))")
    @Override
    public void updateTeachingSubject(Long teachingSubjectId, EditingTeachingSubjectDto editingTeachingSubject) {
        TeachingSubject teachingSubject = teachingSubjectRepository.findById(teachingSubjectId)
                .orElseThrow(() -> {
                    log.warn("Teaching Subject with given id does not exists [teachingSubjectId: {}]", teachingSubjectId);
                    throw new TeachingSubjectNotFoundException(ErrorDesc.TEACHING_SUBJECT_NOT_FOUND.getDesc());
                });

        if (editingTeachingSubject.getSubjectName() != null) {
            teachingSubject.setSubjectName(editingTeachingSubject.getSubjectName());
        }

        if (editingTeachingSubject.getOrganizationId() != null
                && !teachingSubject.getOrganization().getId().equals(editingTeachingSubject.getOrganizationId())) {
            Organization organization = organizationRepository.findById(editingTeachingSubject.getOrganizationId())
                    .orElseThrow(() -> {
                        log.warn("Organization with given id does not exists [organizationId: {}]", editingTeachingSubject.getOrganizationId());
                        throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                    });

            teachingSubject.setOrganization(organization);
        }

        if (editingTeachingSubject.getRemovedTeachers() != null
                && editingTeachingSubject.getRemovedTeachers().size() > 0) {
            editingTeachingSubject.getRemovedTeachers().forEach(_teacherId -> {
                UUID teacherId = UUID.fromString(_teacherId);

                Teacher teacher = (Teacher) userRepository.findById(teacherId)
                        .orElseThrow(() -> {
                            log.warn("Teacher with given id does not exists [teacherId: {}]", teacherId);
                            throw new TeacherNotFoundException(ErrorDesc.TEACHER_NOT_FOUND.getDesc());
                        });

                teacher.removeTeachingSubject(teachingSubject);
            });
        }

        if (editingTeachingSubject.getAddedTeachers() != null
                && editingTeachingSubject.getAddedTeachers().size() > 0) {
            editingTeachingSubject.getAddedTeachers().forEach(_teacherId -> {
                UUID teacherId = UUID.fromString(_teacherId);

                Teacher teacher = (Teacher) userRepository.findById(teacherId)
                        .orElseThrow(() -> {
                            log.warn("Teacher with given id does not exists [teacherId: {}]", teacherId);
                            throw new TeacherNotFoundException(ErrorDesc.TEACHER_NOT_FOUND.getDesc());
                        });

                teacher.addTeachingSubject(teachingSubject);
            });
        }

        teachingSubjectRepository.save(teachingSubject);

        log.info("The Teaching Subject has been updated successfully [teachingSubjectId: {}, performedBy: {}]",
                teachingSubject.getId(), SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @DeletingEntity(domain = TransactionDomain.TEACHING_SUBJECT, action = DomainAction.DELETE_TEACHING_SUBJECT, idArg = "teachingSubjectId")
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:teachingsubjects') || hasAuthority('delete:teachingsubject'))")
    @Override
    public void deleteTeachingSubject(Long teachingSubjectId) {
        TeachingSubject teachingSubject = teachingSubjectRepository.findById(teachingSubjectId)
                .orElseThrow(() -> {
                    log.warn("Teaching Subject with given id does not exists [teachingSubjectId: {}]", teachingSubjectId);
                    throw new TeachingSubjectNotFoundException(ErrorDesc.TEACHING_SUBJECT_NOT_FOUND.getDesc());
                });

        teachingSubject.getTeachers().forEach(teacher -> teacher.getTeachingSubjects().remove(teachingSubject));

        teachingSubjectRepository.deleteById(teachingSubjectId);

        log.info("The Teaching Subject has been deleted successfully [teachingSubjectId: {}, performedBy: {}]",
                teachingSubjectId, SecurityContextHolder.getContext().getAuthentication().getName());
    }

}
