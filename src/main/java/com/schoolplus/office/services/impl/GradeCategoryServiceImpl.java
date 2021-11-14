package com.schoolplus.office.services.impl;

import com.schoolplus.office.annotations.CreatingEntity;
import com.schoolplus.office.annotations.DeletingEntity;
import com.schoolplus.office.annotations.ReadingEntity;
import com.schoolplus.office.annotations.UpdatingEntity;
import com.schoolplus.office.domain.Grade;
import com.schoolplus.office.domain.GradeCategory;
import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.repository.GradeCategoryRepository;
import com.schoolplus.office.repository.GradeRepository;
import com.schoolplus.office.repository.OrganizationRepository;
import com.schoolplus.office.services.GradeCategoryService;
import com.schoolplus.office.web.exceptions.GradeCategoryNotFoundException;
import com.schoolplus.office.web.exceptions.OrganizationNotFoundException;
import com.schoolplus.office.web.mappers.GradeCategoryMapper;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class GradeCategoryServiceImpl implements GradeCategoryService {

    private final GradeCategoryRepository gradeCategoryRepository;
    private final OrganizationRepository organizationRepository;
    private final GradeCategoryMapper gradeCategoryMapper;
    private final GradeRepository gradeRepository;

    @ReadingEntity(domain = TransactionDomain.GRADE_CATEGORY, action = DomainAction.READ_GRADE_CATEGORY, isList = true)
    @PreAuthorize("hasRole('ROLE_ADMIN') || (hasAuthority('manage:grade:categories') || hasAuthority('read:grade:categories'))")
    @Override
    public Page<GradeCategoryDto> getGradeCategoriesByOrganization(Long organizationId, Pageable pageable, String search) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists [organizationId: {}]", organizationId);
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        Page<GradeCategory> gradeCategories;

        if(StringUtils.isEmpty(search) || search.trim().equals("")) {
            gradeCategories = gradeCategoryRepository.findAllByOrganization(organization, pageable);
        } else {
            gradeCategories = gradeCategoryRepository.findAllByOrganizationAndGradeCategoryNameStartingWith(organization, search.trim(), pageable);
        }

        return new PageImpl<>(gradeCategoryMapper.gradeCategoryToGradeCategoryDto(gradeCategories.getContent()),
                pageable,
                gradeCategories.getTotalElements());
    }

    @ReadingEntity(domain = TransactionDomain.GRADE_CATEGORY, action = DomainAction.READ_GRADE_CATEGORY)
    @PreAuthorize("hasRole('ROLE_ADMIN') || (hasAuthority('manage:grade:categories') || hasAuthority('read:grade:category'))")
    @Override
    public GradeCategoryDto getGradeCategory(Long gradeCategoryId) {
        GradeCategory gradeCategory = gradeCategoryRepository.findById(gradeCategoryId)
                .orElseThrow(() -> {
                    log.warn("Grade Category with given id does not exists [gradeCategoryId: {}]", gradeCategoryId);
                    throw new GradeCategoryNotFoundException(ErrorDesc.GRADE_CATEGORY_NOT_FOUND.getDesc());
                });

        return gradeCategoryMapper.gradeCategoryToGradeCategoryDto(gradeCategory);
    }

    @CreatingEntity(domain = TransactionDomain.GRADE_CATEGORY, action = DomainAction.CREATE_GRADE_CATEGORY)
    @PreAuthorize("hasRole('ROLE_ADMIN') || (hasAuthority('manage:grade:categories') || hasAuthority('write:grade:category'))")
    @Override
    public GradeCategoryDto createGradeCategory(CreatingGradeCategoryDto creatingGradeCategoryDto) {
        Organization organization = organizationRepository.findById(creatingGradeCategoryDto.getOrganizationId())
                .orElseThrow(() -> {
                   log.warn("Organization with given id does not exists [organizationId: {}]", creatingGradeCategoryDto.getOrganizationId());
                   throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        GradeCategory gradeCategory = new GradeCategory();
        gradeCategory.setGradeCategoryName(creatingGradeCategoryDto.getGradeCategoryName());
        gradeCategory.setOrganization(organization);

        gradeCategoryRepository.save(gradeCategory);

        log.info("Grade Category has been created successfully [gradeCategoryId: {}, performedBy: {}]",
                gradeCategory.getId(),
                SecurityContextHolder.getContext().getAuthentication().getName());

        return gradeCategoryMapper.gradeCategoryToGradeCategoryDto(gradeCategory);
    }

    @UpdatingEntity(domain = TransactionDomain.ANNOUNCEMENT, action = DomainAction.UPDATE_ANNOUNCEMENT, idArg = "gradeCategoryId")
    @PreAuthorize("hasRole('ROLE_ADMIN') || (hasAuthority('manage:grade:categories') || hasAuthority('update:grade:category'))")
    @Override
    public void editGradeCategory(Long gradeCategoryId, EditingGradeCategoryDto editingGradeCategoryDto) {
        GradeCategory gradeCategory = gradeCategoryRepository.findById(gradeCategoryId)
                .orElseThrow(() -> {
                    log.warn("Grade Category with given id does not exists [gradeCategoryId: {}]", gradeCategoryId);
                    throw new GradeCategoryNotFoundException(ErrorDesc.GRADE_CATEGORY_NOT_FOUND.getDesc());
                });

        if (editingGradeCategoryDto.getGradeCategoryName() != null
                && editingGradeCategoryDto.getGradeCategoryName().trim().length() > 0) {
            gradeCategory.setGradeCategoryName(editingGradeCategoryDto.getGradeCategoryName());
        }

        if (editingGradeCategoryDto.getOrganizationId() != null) {
            Organization organization = organizationRepository.findById(editingGradeCategoryDto.getOrganizationId())
                    .orElseThrow(() -> {
                        log.warn("Organization with given id does not exists [organizationId: {}]", editingGradeCategoryDto.getOrganizationId());
                        throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                    });

            gradeCategory.setOrganization(organization);
        }

        if (editingGradeCategoryDto.getDeletedGrades() != null
                && editingGradeCategoryDto.getDeletedGrades().size() > 0) {
            editingGradeCategoryDto.getDeletedGrades().forEach(deletedGradeId -> {
                if(deletedGradeId != null) {
                    Optional<Grade> optionalGrade = gradeRepository.findById(deletedGradeId);

                    optionalGrade.ifPresent(gradeCategory::removeGrade);
                }
            });
        }

        if (editingGradeCategoryDto.getAddedGrades() != null
                && editingGradeCategoryDto.getAddedGrades().size() > 0) {
            editingGradeCategoryDto.getAddedGrades().forEach(addedGradeId -> {
                if (addedGradeId != null) {
                    Optional<Grade> optionalGrade = gradeRepository.findById(addedGradeId);

                    optionalGrade.ifPresent(gradeCategory::addGrade);
                }
            });
        }

        gradeCategoryRepository.save(gradeCategory);

        log.info("Grade Category has been updated successfully [gradeCategoryId: {}, performedBy: {}]",
                gradeCategoryId,
                SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Transactional
    @DeletingEntity(domain = TransactionDomain.GRADE_CATEGORY, action = DomainAction.DELETE_GRADE_CATEGORY, idArg = "gradeCategoryId")
    @PreAuthorize("hasRole('ROLE_ADMIN') || (hasAuthority('manage:grade:categories') || hasAuthority('delete:grade:category'))")
    @Override
    public void deleteGradeCategory(Long gradeCategoryId) {
        GradeCategory gradeCategory = gradeCategoryRepository.findById(gradeCategoryId)
                .orElseThrow(() -> {
                    log.warn("Grade Category with given id does not exists [gradeCategoryId: {}]", gradeCategoryId);
                    throw new GradeCategoryNotFoundException(ErrorDesc.GRADE_CATEGORY_NOT_FOUND.getDesc());
                });

        gradeCategory.getGrades().forEach(grade -> grade.setGradeCategory(null));

        gradeCategoryRepository.deleteById(gradeCategoryId);

        log.info("Grade Category has been deleted successfully [gradeCategoryId: {}]", gradeCategoryId);
    }

}
