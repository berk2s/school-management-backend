package com.schoolplus.office.services.impl;

import com.schoolplus.office.domain.Classroom;
import com.schoolplus.office.domain.Grade;
import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.repository.ClassroomRepository;
import com.schoolplus.office.repository.GradeRepository;
import com.schoolplus.office.repository.OrganizationRepository;
import com.schoolplus.office.services.GradeService;
import com.schoolplus.office.web.exceptions.ClassroomNotFoundException;
import com.schoolplus.office.web.exceptions.GradeNotFoundException;
import com.schoolplus.office.web.exceptions.OrganizationNotFoundException;
import com.schoolplus.office.web.mappers.GradeMapper;
import com.schoolplus.office.web.models.CreatingGradeDto;
import com.schoolplus.office.web.models.EditingGradeDto;
import com.schoolplus.office.web.models.ErrorDesc;
import com.schoolplus.office.web.models.GradeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class GradeServiceImpl implements GradeService {

    private final GradeRepository gradeRepository;
    private final OrganizationRepository organizationRepository;
    private final ClassroomRepository classroomRepository;
    private final GradeMapper gradeMapper;

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:grades') || hasAuthority('read:grades'))")
    @Override
    public List<GradeDto> getGrades(Pageable pageable) {
        Page<Grade> grades = gradeRepository.findAll(pageable);

        return gradeMapper.gradeToGradeDto(grades.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:grades') || hasAuthority('read:grade'))")
    @Override
    public GradeDto getGrade(Long gradeId) {
        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> {
                    log.warn("Grade with given id does not exists [gradeId: {}]", gradeId);
                    throw new GradeNotFoundException(ErrorDesc.GRADE_NOT_FOUND.getDesc());
                });

        return gradeMapper.gradeToGradeDto(grade);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:grades') || hasAuthority('write:grade'))")
    @Override
    public GradeDto createGrade(CreatingGradeDto creatingGrade) {
        Grade grade = new Grade();
        grade.setGradeName(creatingGrade.getGradeName());

        Organization organization = organizationRepository.findById(creatingGrade.getOrganizationId())
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists [organizationId: {}]", creatingGrade.getOrganizationId());
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        grade.setOrganization(organization);

        if (creatingGrade.getClassRooms() != null && creatingGrade.getClassRooms().size() > 0) {
            creatingGrade.getClassRooms().forEach(classRoomId -> {
                Classroom classroom = classroomRepository.findById(classRoomId)
                        .orElseThrow(() -> {
                            log.warn("Classroom with given id does not exists [classRoomId: {}]", classRoomId);
                            throw new ClassroomNotFoundException(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc());
                        });

                grade.addClassroom(classroom);
            });
        }

        gradeRepository.save(grade);

        log.info("Grade has been created successfully [gradeId: {}, performedBy: {}]", grade.getId(),
                SecurityContextHolder.getContext().getAuthentication().getName());

        return gradeMapper.gradeToGradeDto(grade);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:grades') || hasAuthority('update:grade'))")
    @Override
    public void editGrade(Long gradeId, EditingGradeDto editingGrade) {
        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> {
                    log.warn("Grade with given id does not exists [gradeId: {}]", gradeId);
                    throw new GradeNotFoundException(ErrorDesc.GRADE_NOT_FOUND.getDesc());
                });

        if (editingGrade.getGradeName() != null) {
            grade.setGradeName(editingGrade.getGradeName());
        }

        if (editingGrade.getOrganizationId() != null) {
            Organization organization = organizationRepository.findById(editingGrade.getOrganizationId())
                    .orElseThrow(() -> {
                        log.warn("Organization with given id does not exists [organizationId: {}]", editingGrade.getOrganizationId());
                        throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                    });

            grade.setOrganization(organization);
        }

        if (editingGrade.getRemovedClassrooms() != null
                && editingGrade.getRemovedClassrooms().size() > 0) {
            editingGrade.getRemovedClassrooms().forEach(classRoomId -> {
                Classroom classroom = classroomRepository.findById(classRoomId)
                        .orElseThrow(() -> {
                            log.warn("Classroom with given id does not exists [classRoomId: {}]", classRoomId);
                            throw new ClassroomNotFoundException(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc());
                        });

                grade.removeClassroom(classroom);
            });
        }

        if (editingGrade.getAddedClassrooms() != null
                && editingGrade.getAddedClassrooms().size() > 0) {
            editingGrade.getAddedClassrooms().forEach(classRoomId -> {
                Classroom classroom = classroomRepository.findById(classRoomId)
                        .orElseThrow(() -> {
                            log.warn("Classroom with given id does not exists [classRoomId: {}]", classRoomId);
                            throw new ClassroomNotFoundException(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc());
                        });

                grade.addClassroom(classroom);
            });
        }

        gradeRepository.save(grade);

        log.info("Grade has been edited successfully [gradeId: {}, performedBy: {}]", gradeId,
                SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:grades') || hasAuthority('delete:grade'))")
    @Override
    public void deleteGrade(Long gradeId) {
        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> {
                    log.warn("Grade with given id does not exists [gradeId: {}]", gradeId);
                    throw new GradeNotFoundException(ErrorDesc.GRADE_NOT_FOUND.getDesc());
                });

        grade.getClassrooms().forEach(classroom -> classroom.setGrade(null));

        gradeRepository.deleteById(gradeId);

        log.info("Grade has been deleted successfully [gradeId: {}, performedBy: {}]", gradeId,
                SecurityContextHolder.getContext().getAuthentication().getName());
    }

}
