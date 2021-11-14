package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.Grade;
import com.schoolplus.office.web.models.GradeDto;
import org.mapstruct.*;

import java.util.List;

@Mapper(uses = {ClassroomMapper.class, OrganizationMapper.class, GradeCategoryMapper.class})
public interface GradeMapper {

    @Mappings({
            @Mapping(source = "id", target = "gradeId"),
            @Mapping(source = "gradeName", target = "gradeName"),
            @Mapping(source = "classrooms", target = "classrooms", qualifiedByName = "WithoutStudentsAndParents"),
            @Mapping(source = "organization", target = "organization", qualifiedByName = "WithoutDetails"),
            @Mapping(source = "gradeCategory", target = "gradeCategory", qualifiedByName = "WithoutDetailsOutside")
    })
    GradeDto gradeToGradeDto(Grade grade);

    @Mappings({
            @Mapping(source = "id", target = "gradeId"),
            @Mapping(source = "gradeName", target = "gradeName"),
            @Mapping(source = "classrooms", target = "classrooms", qualifiedByName = "WithoutStudentsAndParentsList"),
            @Mapping(source = "organization", target = "organization", qualifiedByName = "WithoutDetails"),
            @Mapping(source = "gradeCategory", target = "gradeCategory", qualifiedByName = "WithoutDetailsOutside")
    })
    List<GradeDto> gradeToGradeDto(List<Grade> grade);

    @Named("WithoutDetails")
    @Mappings({
            @Mapping(source = "id", target = "gradeId"),
            @Mapping(source = "gradeName", target = "gradeName"),
            @Mapping(source = "classrooms", target = "classrooms", ignore = true),
            @Mapping(source = "organization", target = "organization", ignore = true),
            @Mapping(source = "gradeCategory", target = "gradeCategory", qualifiedByName = "WithoutDetailsOutside")
    })
    GradeDto gradeToGradeDtoWithoutDetails(Grade grade);

    @Named("WithoutDetailsList")
    @Mappings({
            @Mapping(source = "id", target = "gradeId"),
            @Mapping(source = "gradeName", target = "gradeName"),
            @Mapping(source = "classrooms", target = "classrooms", ignore = true),
            @Mapping(source = "organization", target = "organization", ignore = true),
    })
    @IterableMapping(qualifiedByName = "WithoutDetails")
    List<GradeDto> gradeToGradeDtoWithoutDetailsList(List<Grade> grade);

}
