package com.schoolplus.office.services;

import com.schoolplus.office.web.models.CreatingGradeCategoryDto;
import com.schoolplus.office.web.models.EditingGradeCategoryDto;
import com.schoolplus.office.web.models.GradeCategoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GradeCategoryService {

    Page<GradeCategoryDto> getGradeCategoriesByOrganization(Long organizationId, Pageable pageable, String search);

    GradeCategoryDto getGradeCategory(Long gradeCategoryId);

    GradeCategoryDto createGradeCategory(CreatingGradeCategoryDto creatingGradeCategoryDto);

    void editGradeCategory(Long gradeCategoryId, EditingGradeCategoryDto editingGradeCategoryDto);

    void deleteGradeCategory(Long gradeCategoryId);

}
