package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.Lesson;
import com.schoolplus.office.web.models.LessonDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(uses = {OrganizationMapper.class})
public interface LessonMapper {

    @Mappings({
            @Mapping(target = "lessonId", source = "id")
    })
    LessonDto lessonToLessonDto(Lesson lesson);

}
