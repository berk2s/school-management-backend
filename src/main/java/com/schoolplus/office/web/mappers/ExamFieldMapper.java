package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.ExamField;
import com.schoolplus.office.web.models.ExamFieldDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface ExamFieldMapper {

    @Mappings({
            @Mapping(target = "examFieldId", source = "id"),
            @Mapping(target = "createdAt", source = "createdAt"),
            @Mapping(target = "lastModifiedAt", source = "lastModifiedAt"),
            @Mapping(target = "examSkeleton", source = "examSkeleton", ignore = true)
    })
    ExamFieldDto examFieldToExamFieldDto(ExamField examField);

    @Named("WithoutExamFieldSkeleton")
    @Mappings({
            @Mapping(target = "examFieldId", source = "id"),
            @Mapping(target = "createdAt", source = "createdAt"),
            @Mapping(target = "lastModifiedAt", source = "lastModifiedAt"),
            @Mapping(target = "examSkeleton", source = "examSkeleton", ignore = true)
    })
    ExamFieldDto examFieldToExamFieldDtoWithoutExamSkeleton(ExamField examField);

    @Mappings({
            @Mapping(target = "examFieldId", source = "id"),
            @Mapping(target = "createdAt", source = "createdAt"),
            @Mapping(target = "lastModifiedAt", source = "lastModifiedAt"),
            @Mapping(target = "examSkeleton", source = "examSkeleton", ignore = true)
    })
    List<ExamFieldDto> examFieldToExamFieldDto(List<ExamField> examFields);

}