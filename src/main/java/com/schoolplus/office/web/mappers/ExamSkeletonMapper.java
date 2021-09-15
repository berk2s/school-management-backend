package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.ExamSkeleton;
import com.schoolplus.office.web.models.ExamSkeletonDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper( uses = {ExamFieldMapper.class})
public interface ExamSkeletonMapper {

    @Mappings({
            @Mapping(target = "examSkeletonId", source = "id"),
            @Mapping(target = "createdAt", source = "createdAt"),
            @Mapping(target = "lastModifiedAt", source = "lastModifiedAt"),
            @Mapping(target = "examFields", source = "examFields", qualifiedByName = "WithoutExamFieldSkeleton")
    })
    ExamSkeletonDto examSkeletonDtoToExamSkeleton(ExamSkeleton examSkeleton);


    @Mappings({
            @Mapping(target = "examSkeletonId", source = "id"),
            @Mapping(target = "createdAt", source = "createdAt"),
            @Mapping(target = "lastModifiedAt", source = "lastModifiedAt"),
            @Mapping(target = "examFields", source = "examFields", qualifiedByName = "WithoutExamFieldSkeleton")
    })
    List<ExamSkeletonDto> examSkeletonDtoToExamSkeleton(List<ExamSkeleton> examSkeleton);


}
