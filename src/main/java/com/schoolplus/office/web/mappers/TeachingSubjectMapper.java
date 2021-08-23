package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.TeachingSubject;
import com.schoolplus.office.web.models.TeachingSubjectDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(uses = TeacherMapper.class)
public interface TeachingSubjectMapper {

    @Mappings({
            @Mapping(source = "id", target = "teachingSubjectId"),
            @Mapping(source = "subjectName", target = "subjectName"),
            @Mapping(source = "teachers", target = "teachers"),
    })
    TeachingSubjectDto teachingSubjectToTeachingSubjectDto(TeachingSubject teachingSubject);

}
