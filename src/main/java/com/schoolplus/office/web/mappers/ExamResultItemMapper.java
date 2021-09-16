package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.ExamResultItem;
import com.schoolplus.office.web.models.ExamResultItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(uses = {StudentMapper.class})
public interface ExamResultItemMapper {

    @Mappings({
            @Mapping(target = "examResultItemId", source = "id"),
            @Mapping(target = "student", source = "student"),
            @Mapping(target = "createdAt", source = "createdAt"),
            @Mapping(target = "lastModifiedAt", source = "lastModifiedAt")
    })
    ExamResultItemDto examResultItemToExamResultItemDto(ExamResultItem examResultItem);

}
