package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.ExamType;
import com.schoolplus.office.web.models.ExamTypeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface ExamTypeMapper {

    @Mappings({
            @Mapping(source = "id", target = "examTypeId"),
            @Mapping(source = "createdAt", target = "createdAt"),
            @Mapping(source = "lastModifiedAt", target = "lastModifiedAt"),
    })
    ExamTypeDto examTypeToExamTypeDto(ExamType examType);

    @Named("WithoutOrganization")
    @Mappings({
            @Mapping(source = "id", target = "examTypeId"),
            @Mapping(source = "createdAt", target = "createdAt"),
            @Mapping(source = "lastModifiedAt", target = "lastModifiedAt"),
            @Mapping(source = "organization", target = "organization", ignore = true),
    })
    ExamTypeDto examTypeToExamTypeDtoWithoutOrganization(ExamType examType);

    @Mappings({
            @Mapping(source = "id", target = "examTypeId"),
            @Mapping(source = "createdAt", target = "createdAt"),
            @Mapping(source = "lastModifiedAt", target = "lastModifiedAt"),
    })
    List<ExamTypeDto> examTypeToExamTypeDto(List<ExamType> examType);

}
