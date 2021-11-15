package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.TeachingSubject;
import com.schoolplus.office.web.models.TeachingSubjectDto;
import org.mapstruct.*;

import java.util.List;

@Mapper(uses = {TeacherMapper.class, OrganizationMapper.class})
public interface TeachingSubjectMapper {

    @Named("Normal")
    @Mappings({
            @Mapping(source = "id", target = "teachingSubjectId"),
            @Mapping(source = "subjectName", target = "subjectName"),
            @Mapping(source = "teachers", target = "teachers"),
            @Mapping(source = "organization", target = "organization", qualifiedByName = "WithoutDetails"),
    })
    TeachingSubjectDto teachingSubjectToTeachingSubjectDto(TeachingSubject teachingSubject);

    @Named("WithoutDetails")
    @Mappings({
            @Mapping(source = "id", target = "teachingSubjectId"),
            @Mapping(source = "subjectName", target = "subjectName"),
            @Mapping(source = "teachers", target = "teachers", ignore = true),
            @Mapping(source = "organization", target = "organization", ignore = true),
    })
    TeachingSubjectDto teachingSubjectToTeachingSubjectDtoWithoutDetails(TeachingSubject teachingSubject);

    @Mappings({
            @Mapping(source = "id", target = "teachingSubjectId"),
            @Mapping(source = "subjectName", target = "subjectName"),
            @Mapping(source = "teachers", target = "teachers"),
            @Mapping(source = "organization", target = "organization"),
    })
    @IterableMapping(qualifiedByName = "Normal")
    List<TeachingSubjectDto> teachingSubjectToTeachingSubjectDto(List<TeachingSubject> teachingSubject);

    @Mappings({
            @Mapping(source = "id", target = "teachingSubjectId"),
            @Mapping(source = "subjectName", target = "subjectName"),
            @Mapping(source = "teachers", target = "teachers"),
            @Mapping(source = "organization", target = "organization"),
    })
    @IterableMapping(qualifiedByName = "WithoutDetails")
    List<TeachingSubjectDto> teachingSubjectToTeachingSubjectDtoWithoutDetailsList(List<TeachingSubject> teachingSubject);

}
