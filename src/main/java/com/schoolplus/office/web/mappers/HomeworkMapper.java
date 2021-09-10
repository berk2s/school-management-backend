package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.Homework;
import com.schoolplus.office.web.models.HomeworkDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(uses = {ClassroomMapper.class, TeacherMapper.class, SyllabusMapper.class})
public interface HomeworkMapper {

    @Mappings({
            @Mapping(target = "homeworkId", source = "id"),
            @Mapping(target = "createdAt", source = "createdAt"),
            @Mapping(target = "lastModifiedAt", source = "lastModifiedAt"),
    })
    HomeworkDto homeworkToHomeworkDto(Homework homework);

    @Mappings({
            @Mapping(target = "homeworkId", source = "id"),
            @Mapping(target = "createdAt", source = "createdAt"),
            @Mapping(target = "lastModifiedAt", source = "lastModifiedAt"),
    })
    List<HomeworkDto> homeworkToHomeworkDto(List<Homework> homeworks);

}
