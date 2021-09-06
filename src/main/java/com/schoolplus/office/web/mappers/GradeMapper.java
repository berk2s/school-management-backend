package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.Grade;
import com.schoolplus.office.web.models.GradeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.List;

@Mapper(uses = {TeacherMapper.class, StudentMapper.class})
public interface GradeMapper {

    @Mappings({
            @Mapping(source = "id", target = "gradeId"),
            @Mapping(source = "gradeTag", target = "gradeTag"),
            @Mapping(target = "gradeType", expression = "java( grade.getGradeType().getType() )"),
            @Mapping(target = "gradeLevel", expression = "java( grade.getGradeLevel().getGradeYear() )"),
            @Mapping(source = "advisorTeacher", target = "advisorTeacher"),
            @Mapping(source = "organization", target = "organization"),
            @Mapping(target = "students", qualifiedByName="WithoutParents"),
    })
    GradeDto gradeToGradeDto(Grade grade);

    @Named("WithoutStudents")
    @Mappings({
            @Mapping(source = "id", target = "gradeId"),
            @Mapping(source = "gradeTag", target = "gradeTag"),
            @Mapping(target = "gradeType", expression = "java( grade.getGradeType().getType() )"),
            @Mapping(target = "gradeLevel", expression = "java( grade.getGradeLevel().getGradeYear() )"),
            @Mapping(source = "advisorTeacher", target = "advisorTeacher"),
            @Mapping(source = "organization", target = "organization"),
            @Mapping(source = "students", target = "students", ignore = true),
    })
    GradeDto gradeToGradeDtoWithoutStudents(Grade grade);

    @Mappings({
            @Mapping(source = "id", target = "gradeId"),
            @Mapping(source = "gradeTag", target = "gradeTag"),
            @Mapping(target = "gradeType", expression = "java( grade.getGradeType().getType() )"),
            @Mapping(target = "gradeLevel", expression = "java( grade.getGradeLevel().getGradeYear() )"),
            @Mapping(target = "advisorTeacher", source = "advisorTeacher"),
            @Mapping(source = "organization", target = "organization"),
            @Mapping(target = "students", source="WithoutParents"),
    })
    List<GradeDto> gradeToGradeDto(List<Grade> grade);

}
