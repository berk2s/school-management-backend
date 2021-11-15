package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.Lesson;
import com.schoolplus.office.web.models.LessonDto;
import org.mapstruct.*;

import java.util.List;

@Mapper(uses = {OrganizationMapper.class})
public interface LessonMapper {

    @Named("Normal")
    @Mappings({
            @Mapping(target = "lessonId", source = "id"),
            @Mapping(target = "createdAt", source = "createdAt"),
            @Mapping(target = "lastModifiedAt", source = "lastModifiedAt"),
            @Mapping(target = "organization", source = "organization", qualifiedByName = "WithoutDetails")
    })
    LessonDto lessonToLessonDto(Lesson lesson);

    @Mappings({
            @Mapping(target = "lessonId", source = "id"),
            @Mapping(target = "createdAt", source = "createdAt"),
            @Mapping(target = "lastModifiedAt", source = "lastModifiedAt")
    })
    @IterableMapping(qualifiedByName = "Normal")
    List<LessonDto> lessonToLessonDto(List<Lesson> lesson);

}
