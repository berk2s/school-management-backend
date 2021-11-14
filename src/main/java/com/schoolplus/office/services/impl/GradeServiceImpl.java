package com.schoolplus.office.services.impl;

import com.schoolplus.office.annotations.CreatingEntity;
import com.schoolplus.office.annotations.DeletingEntity;
import com.schoolplus.office.annotations.ReadingEntity;
import com.schoolplus.office.annotations.UpdatingEntity;
import com.schoolplus.office.domain.Classroom;
import com.schoolplus.office.domain.Grade;
import com.schoolplus.office.domain.GradeCategory;
import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.repository.ClassroomRepository;
import com.schoolplus.office.repository.GradeCategoryRepository;
import com.schoolplus.office.repository.GradeRepository;
import com.schoolplus.office.repository.OrganizationRepository;
import com.schoolplus.office.services.GradeService;
import com.schoolplus.office.web.exceptions.ClassroomNotFoundException;
import com.schoolplus.office.web.exceptions.GradeCategoryNotFoundException;
import com.schoolplus.office.web.exceptions.GradeNotFoundException;
import com.schoolplus.office.web.exceptions.OrganizationNotFoundException;
import com.schoolplus.office.web.mappers.GradeMapper;
import com.schoolplus.office.web.models.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    private final GradeCategoryRepository gradeCategoryRepository;
    private final ClassroomRepository classroomRepository;
    private final GradeMapper gradeMapper;

    @ReadingEntity(domain = TransactionDomain.GRADE, action = DomainAction.READ_GRADES, isList = true)
    @PreAuthorize("hasRole('ADMIN') && (hasAuthority('manage:grades') || hasAuthority('read:grades'))")
    @Override
    public Page<GradeDto> getGradesByOrganization(Long organizationId, Pageable pageable, String search) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists [organizationId: {}]", organizationId);
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        Page<Grade> grades;

        if (StringUtils.isEmpty(search) || search.trim().equals("")) {
            grades = gradeRepository.findAllByOrganization(organization, pageable);
        } else {
            grades = gradeRepository
                    .findAllByOrganizationAndGradeNameStartsWith(organization, search.trim(), pageable);
        }

        return new PageImpl<>(
                gradeMapper.gradeToGradeDtoWithoutDetailsList(grades.getContent()),
                pageable,
                grades.getTotalElements());
    }

    @ReadingEntity(domain = TransactionDomain.GRADE, action = DomainAction.READ_GRADES, isList = true)
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:grades') || hasAuthority('read:grades'))")
    @Override
    public List<GradeDto> getGrades(Pageable pageable) {
        Page<Grade> grades = gradeRepository.findAll(pageable);

        return gradeMapper.gradeToGradeDto(grades.getContent());
    }

    @ReadingEntity(domain = TransactionDomain.GRADE, action = DomainAction.READ_GRADE)
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

    @CreatingEntity(domain = TransactionDomain.GRADE, action = DomainAction.CREATE_GRADE)
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

        GradeCategory gradeCategory = gradeCategoryRepository.findById(creatingGrade.getGradeCategoryId())
                .orElseThrow(() -> {
                    log.warn("Grade Category with given id does not exists [gradeCategoryId: {}]", creatingGrade.getGradeCategoryId());
                    throw new GradeCategoryNotFoundException(ErrorDesc.GRADE_CATEGORY_NOT_FOUND.getDesc());
                });

        grade.setGradeCategory(gradeCategory);

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

    @UpdatingEntity(domain = TransactionDomain.GRADE, action = DomainAction.UPDATE_GRADE, idArg = "gradeId")
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

        if (editingGrade.getNewGradeCategoryId() != null) {
            GradeCategory gradeCategory = gradeCategoryRepository.findById(editingGrade.getNewGradeCategoryId())
                    .orElseThrow(() -> {
                        log.warn("Grade Category with given id does not exists [gradeCategoryId: {}]", editingGrade.getNewGradeCategoryId());
                        throw new GradeCategoryNotFoundException(ErrorDesc.GRADE_CATEGORY_NOT_FOUND.getDesc());
                    });

            grade.setGradeCategory(gradeCategory);
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

    @DeletingEntity(domain = TransactionDomain.GRADE, action = DomainAction.DELETE_GRADE, idArg = "gradeId")
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
