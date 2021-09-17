package com.schoolplus.office.services.impl;

import com.schoolplus.office.annotations.CreatingEntity;
import com.schoolplus.office.annotations.DeletingEntity;
import com.schoolplus.office.annotations.ReadingEntity;
import com.schoolplus.office.annotations.UpdatingEntity;
import com.schoolplus.office.domain.*;
import com.schoolplus.office.repository.*;
import com.schoolplus.office.services.ContinuityService;
import com.schoolplus.office.web.exceptions.*;
import com.schoolplus.office.web.mappers.ContinuityMapper;
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
public class ContinuityServiceImpl implements ContinuityService {

    private final ContinuityRepository continuityRepository;
    private final SyllabusRepository syllabusRepository;
    private final ClassroomRepository classroomRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final ContinuityMapper continuityMapper;

    @ReadingEntity(domain = TransactionDomain.COUNTINUITY, action = DomainAction.READ_CONTINUITY)
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:continuities') || hasAuthority('read:continuity'))")
    @Override
    public ContinuityDto getContinuity(UUID continuityId) {
        Continuity continuity = continuityRepository.findById(continuityId)
                .orElseThrow(() -> {
                    log.warn("Continuity with given id does not exists [continuityId: {}]", continuityId);
                    throw new ContinuityNotFoundException(ErrorDesc.CONTINUITY_NOT_FOUND.getDesc());
                });

        return continuityMapper.continuityToContinuityDto(continuity);
    }

    @ReadingEntity(domain = TransactionDomain.COUNTINUITY, action = DomainAction.READ_CONTINUITIES_BY_SYLLABUS, isList = true)
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:continuities') || hasAuthority('read:continuity'))")
    @Override
    public List<ContinuityDto> getContinuityBySyllabus(Long syllabusId, Pageable pageable) {
        Syllabus syllabus = syllabusRepository.findById(syllabusId)
                .orElseThrow(() -> {
                    log.warn("Syllabus with given id does not exists [syllabusId: {}]", syllabusId);
                    throw new SyllabusNotFoundException(ErrorDesc.SYLLABUS_NOT_FOUND.getDesc());
                });

        Page<Continuity> continuities = continuityRepository.findAllBySyllabus(syllabus, pageable);

        return continuityMapper.continuityToContinuityDto(continuities.getContent());
    }

    @ReadingEntity(domain = TransactionDomain.COUNTINUITY, action = DomainAction.READ_CONTINUITIES_BY_CLASSROOM, isList = true)
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:continuities') || hasAuthority('read:continuity'))")
    @Override
    public List<ContinuityDto> getContinuityByClassroom(Long classroomId, Pageable pageable) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> {
                    log.warn("Classroom with given id does not exists [classRoomId: {}]", classroomId);
                    throw new ClassroomNotFoundException(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc());
                });

        Page<Continuity> continuities = continuityRepository.findAllByClassroom(classroom, pageable);

        return continuityMapper.continuityToContinuityDto(continuities.getContent());
    }

    @ReadingEntity(domain = TransactionDomain.COUNTINUITY, action = DomainAction.READ_CONTINUITIES_BY_STUDENT, isList = true)
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:continuities') || hasAuthority('read:continuity'))")
    @Override
    public List<ContinuityDto> getContinuityByStudent(UUID studentId, Pageable pageable) {
        Student student = (Student) userRepository.findById(studentId)
                .orElseThrow(() -> {
                    log.warn("Student with given id does not exists [studentId: {}]", studentId);
                    throw new StudentNotFoundException(ErrorDesc.STUDENT_NOT_FOUND.getDesc());
                });

        Page<Continuity> continuities = continuityRepository.findAllByStudent(student, pageable);

        return continuityMapper.continuityToContinuityDto(continuities.getContent());
    }

    @ReadingEntity(domain = TransactionDomain.COUNTINUITY, action = DomainAction.READ_CONTINUITIES_BY_ORGANIZATION, isList = true)
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:continuities') || hasAuthority('create:continuity'))")
    @Override
    public List<ContinuityDto> getContinuityByOrganization(Long organizationId, Pageable pageable) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists [organizationId: {}]", organizationId);
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        Page<Continuity> continuities = continuityRepository.findAllByOrganization(organization, pageable);

        return continuityMapper.continuityToContinuityDto(continuities.getContent());
    }

    @CreatingEntity(domain = TransactionDomain.COUNTINUITY, action = DomainAction.CREATE_CONTINUITY)
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:continuities') || hasAuthority('create:continuity'))")
    @Transactional
    @Override
    public ContinuityDto createContinuity(CreatingContinuityDto creatingContinuity) {
        Continuity continuity = new Continuity();
        continuity.setIsAbsent(creatingContinuity.getIsAbsent());

        Syllabus syllabus = syllabusRepository.findById(creatingContinuity.getSyllabusId())
                .orElseThrow(() -> {
                    log.warn("Syllabus with given id does not exists [syllabusId: {}]", creatingContinuity.getSyllabusId());
                    throw new SyllabusNotFoundException(ErrorDesc.SYLLABUS_NOT_FOUND.getDesc());
                });

        continuity.setSyllabus(syllabus);

        Classroom classroom = classroomRepository.findById(creatingContinuity.getClassroomId())
                .orElseThrow(() -> {
                    log.warn("Classroom with given id does not exists [classRoomId: {}]", creatingContinuity.getClassroomId());
                    throw new ClassroomNotFoundException(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc());
                });

        continuity.setClassroom(classroom);

        Student student = (Student) userRepository.findById(UUID.fromString(creatingContinuity.getStudentId()))
                .orElseThrow(() -> {
                    log.warn("Student with given id does not exists [studentId: {}]", creatingContinuity.getStudentId());
                    throw new StudentNotFoundException(ErrorDesc.STUDENT_NOT_FOUND.getDesc());
                });

        continuity.setStudent(student);

        Organization organization = organizationRepository.findById(creatingContinuity.getOrganizationId())
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists [organizationId: {}]", creatingContinuity.getOrganizationId());
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        continuity.setOrganization(organization);

        Continuity savedContinuity = continuityRepository.save(continuity);

        log.info("Continuity has been created successfully [continuityId: {}, performedBy: {}]", continuity.getId(),
                SecurityContextHolder.getContext().getAuthentication().getName());

        return continuityMapper.continuityToContinuityDto(savedContinuity);
    }

    @UpdatingEntity(domain = TransactionDomain.COUNTINUITY, action = DomainAction.UPDATE_CONTINUITY, idArg = "continuityId")
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:continuities') || hasAuthority('update:continuity'))")
    @Override
    public void editContinuity(UUID continuityId, EditingContinuityDto editingContinuity) {
        Continuity continuity = continuityRepository.findById(continuityId)
                .orElseThrow(() -> {
                   log.warn("Continuity with given id does not exists [continuityId: {}]", continuityId);
                   throw new ContinuityNotFoundException(ErrorDesc.CONTINUITY_NOT_FOUND.getDesc());
                });

        if (editingContinuity.getIsAbsent() != null) {
            continuity.setIsAbsent(editingContinuity.getIsAbsent());
        }

        if (editingContinuity.getSyllabusId() != null) {
            Syllabus syllabus = syllabusRepository.findById(editingContinuity.getSyllabusId())
                    .orElseThrow(() -> {
                        log.warn("Syllabus with given id does not exists [syllabusId: {}]", editingContinuity.getSyllabusId());
                        throw new SyllabusNotFoundException(ErrorDesc.SYLLABUS_NOT_FOUND.getDesc());
                    });

            continuity.setSyllabus(syllabus);
        }

        if (editingContinuity.getClassroomId() != null) {
            Classroom classroom = classroomRepository.findById(editingContinuity.getClassroomId())
                    .orElseThrow(() -> {
                        log.warn("Classroom with given id does not exists [classRoomId: {}]", editingContinuity.getClassroomId());
                        throw new ClassroomNotFoundException(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc());
                    });

            continuity.setClassroom(classroom);
        }

        if (editingContinuity.getStudentId() != null) {
            Student student = (Student) userRepository.findById(UUID.fromString(editingContinuity.getStudentId()))
                    .orElseThrow(() -> {
                        log.warn("Student with given id does not exists [studentId: {}]", editingContinuity.getStudentId());
                        throw new StudentNotFoundException(ErrorDesc.STUDENT_NOT_FOUND.getDesc());
                    });

            continuity.setStudent(student);
        }

        if (editingContinuity.getOrganizationId() != null) {
            Organization organization = organizationRepository.findById(editingContinuity.getOrganizationId())
                    .orElseThrow(() -> {
                        log.warn("Organization with given id does not exists [organizationId: {}]", editingContinuity.getOrganizationId());
                        throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                    });

            continuity.setOrganization(organization);
        }

        continuityRepository.save(continuity);

        log.warn("The Continuity has been updated successfully [continuityId: {}, performedBy: {}]", continuity,
                SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @DeletingEntity(domain = TransactionDomain.COUNTINUITY, action = DomainAction.DELETE_CONTINUITY, idArg = "continuityId")
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:continuities') || hasAuthority('delete:continuity'))")
    @Override
    public void deleteContinuity(UUID continuityId) {
        if (!continuityRepository.existsById(continuityId)) {
            log.warn("Continuity with given id does not exists [continuityId: {}]", continuityId);
            throw new ContinuityNotFoundException(ErrorDesc.CONTINUITY_NOT_FOUND.getDesc());
        }

        continuityRepository.deleteById(continuityId);

        log.warn("The Continuity has been deleted successfully [contiunityId: {}, performedBy: {}]", continuityId,
                SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
