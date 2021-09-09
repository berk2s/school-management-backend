package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.Syllabus;
import com.schoolplus.office.web.models.SyllabusDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(uses = {ClassroomMapper.class, TeacherMapper.class, LessonMapper.class, OrganizationMapper.class})
public interface SyllabusMapper {

    @Mappings({
            @Mapping(target = "syllabusId", source = "id"),
            @Mapping(target = "syllabusStartDate", source = "syllabusStartDate"),
            @Mapping(target = "syllabusEndDate", source = "syllabusEndDate"),
    })
    SyllabusDto syllabusToSyllabusDto(Syllabus syllabus);

    @Mappings({
            @Mapping(target = "syllabusId", source = "id"),
            @Mapping(target = "syllabusStartDate", source = "syllabusStartDate"),
            @Mapping(target = "syllabusEndDate", source = "syllabusEndDate"),
    })
    List<SyllabusDto> syllabusToSyllabusDto(List<Syllabus> syllabus);

}
