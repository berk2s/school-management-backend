package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.PersonalHomework;
import com.schoolplus.office.web.models.PersonalHomeworkDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(uses = {StudentMapper.class, TeacherMapper.class, LessonMapper.class})
public interface PersonalHomeworkMapper {

    @Mappings({
            @Mapping(target = "personalHomeworkId", source = "id"),
            @Mapping(target = "createdAt", source = "createdAt"),
            @Mapping(target = "lastModifiedAt", source = "lastModifiedAt"),
    })
    PersonalHomeworkDto personalHomeworkToPersonalHomeworkDto(PersonalHomework personalHomework);

    @Mappings({
            @Mapping(target = "personalHomeworkId", source = "id"),
            @Mapping(target = "createdAt", source = "createdAt"),
            @Mapping(target = "lastModifiedAt", source = "lastModifiedAt"),
    })
    List<PersonalHomeworkDto> personalHomeworkToPersonalHomeworkDto(List<PersonalHomework> personalHomework);

}
