package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.Grade;
import com.schoolplus.office.web.models.GradeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.List;

@Mapper(uses = {ClassroomMapper.class, OrganizationMapper.class})
public interface GradeMapper {

    @Mappings({
            @Mapping(source = "id", target = "gradeId"),
            @Mapping(source = "gradeName", target = "gradeName"),
            @Mapping(source = "classrooms", target = "classrooms", qualifiedByName = "WithoutStudentsAndParents"),
            @Mapping(source = "organization", target = "organization"),
    })
    GradeDto gradeToGradeDto(Grade grade);

    @Mappings({
            @Mapping(source = "id", target = "gradeId"),
            @Mapping(source = "gradeName", target = "gradeName"),
            @Mapping(source = "classrooms", target = "classrooms", qualifiedByName = "WithoutStudentsAndParentsList"),
            @Mapping(source = "organization", target = "organization"),
    })
    List<GradeDto> gradeToGradeDto(List<Grade> grade);

    @Named("WithoutDetails")
    @Mappings({
            @Mapping(source = "id", target = "gradeId"),
            @Mapping(source = "gradeName", target = "gradeName"),
            @Mapping(source = "classrooms", target = "classrooms", ignore = true),
            @Mapping(source = "organization", target = "organization", ignore = true),
    })
    GradeDto gradeToGradeDtoWithoutDetails(Grade grade);

    @Named("WithoutDetailsList")
    @Mappings({
            @Mapping(source = "id", target = "gradeId"),
            @Mapping(source = "gradeName", target = "gradeName"),
            @Mapping(source = "classrooms", target = "classrooms", ignore = true),
            @Mapping(source = "organization", target = "organization", ignore = true),
    })
    List<GradeDto> gradeToGradeDtoWithoutDetailsList(List<Grade> grade);

}
