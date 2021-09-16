package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.ExamResult;
import com.schoolplus.office.web.models.ExamResultDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.List;

@Mapper(uses = {ExamMapper.class, ExamResultItemMapper.class})
public interface ExamResultMapper {

    @Mappings({
            @Mapping(target = "examResultId", source = "id"),
            @Mapping(target = "examResultItems", source = "examResultItems"),
            @Mapping(target = "createdAt", source = "createdAt"),
            @Mapping(target = "lastModifiedAt", source = "lastModifiedAt")
    })
    ExamResultDto examResultToExamResultDto(ExamResult examResult);

    @Named("WithoutItems")
    @Mappings({
            @Mapping(target = "examResultId", source = "id"),
            @Mapping(target = "examResultItems", source = "examResultItems", ignore = true),
            @Mapping(target = "createdAt", source = "createdAt"),
            @Mapping(target = "lastModifiedAt", source = "lastModifiedAt")
    })
    ExamResultDto examResultToExamResultDtoWithoutItems(ExamResult examResult);

    @Named("WithoutItemsList")
    @Mappings({
            @Mapping(target = "examResultId", source = "id"),
            @Mapping(target = "examResultItems", source = "examResultItems", ignore = true),
            @Mapping(target = "createdAt", source = "createdAt"),
            @Mapping(target = "lastModifiedAt", source = "lastModifiedAt")
    })
    List<ExamResultDto> examResultToExamResultDtoWithoutItemsList(List<ExamResult> examResults);

}
