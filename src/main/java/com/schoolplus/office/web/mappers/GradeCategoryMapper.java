package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.GradeCategory;
import com.schoolplus.office.web.models.GradeCategoryDto;
import org.mapstruct.*;

import java.util.List;

@Mapper(uses = {GradeMapper.class, OrganizationMapper.class})
public interface GradeCategoryMapper {

    @Named("WithoutDetails")
    @Mappings({
            @Mapping(source = "id", target = "gradeCategoryId"),
            @Mapping(source = "organization", target = "organization", qualifiedByName = "WithoutDetails"),
            @Mapping(source = "grades", target = "grades", qualifiedByName = "WithoutDetailsList"),
    })
    GradeCategoryDto gradeCategoryToGradeCategoryDto(GradeCategory gradeCategory);

    @Mappings({
            @Mapping(source = "id", target = "gradeCategoryId"),
            @Mapping(source = "organization", target = "organization", qualifiedByName = "WithoutDetails"),
            @Mapping(source = "grades", target = "grades", qualifiedByName = "WithoutDetailsList")
    })
    @IterableMapping(qualifiedByName = "WithoutDetails")
    List<GradeCategoryDto> gradeCategoryToGradeCategoryDto(List<GradeCategory> gradeCategories);

    @Named("WithoutDetailsOutside")
    @Mappings({
            @Mapping(source = "id", target = "gradeCategoryId"),
            @Mapping(source = "organization", target = "organization", ignore = true),
            @Mapping(source = "grades", target = "grades", ignore = true),
            @Mapping(source = "createdAt", target = "createdAt", ignore = true),
            @Mapping(source = "lastModifiedAt", target = "lastModifiedAt", ignore = true),
    })
    GradeCategoryDto gradeCategoryToGradeCategoryDtoOutside(GradeCategory gradeCategory);

    @Named("WithoutDetailsListOutside")
    @Mappings({
            @Mapping(source = "id", target = "gradeCategoryId"),
            @Mapping(source = "organization", target = "organization", ignore = true),
            @Mapping(source = "grades", target = "grades", ignore = true),
            @Mapping(source = "createdAt", target = "createdAt", ignore = true),
            @Mapping(source = "lastModifiedAt", target = "lastModifiedAt", ignore = true),
    })
    @IterableMapping(qualifiedByName = "WithoutDetailsOutside")
    List<GradeCategoryDto> gradeCategoryToGradeCategoryDtoListOutside(List<GradeCategory> gradeCategories);


}
