package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.Lesson;
import com.schoolplus.office.web.models.LessonDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(uses = {OrganizationMapper.class})
public interface LessonMapper {

    @Mappings({
            @Mapping(target = "lessonId", source = "id"),
            @Mapping(target = "createdAt", source = "createdAt"),
            @Mapping(target = "lastModifiedAt", source = "lastModifiedAt")
    })
    LessonDto lessonToLessonDto(Lesson lesson);

    @Mappings({
            @Mapping(target = "lessonId", source = "id"),
            @Mapping(target = "createdAt", source = "createdAt"),
            @Mapping(target = "lastModifiedAt", source = "lastModifiedAt")
    })
    List<LessonDto> lessonToLessonDto(List<Lesson> lesson);

}
