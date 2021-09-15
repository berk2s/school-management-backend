package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.Exam;
import com.schoolplus.office.domain.ExamSkeleton;
import com.schoolplus.office.web.models.ExamDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(uses = {ExamTypeMapper.class, ExamSkeletonMapper.class})
public interface ExamMapper {

    @Mappings({
            @Mapping(source = "id", target = "examId"),
            @Mapping(source = "examName", target = "examName"),
            @Mapping(source = "createdAt", target = "createdAt"),
            @Mapping(source = "lastModifiedAt", target = "lastModifiedAt"),
            @Mapping(source = "organization", target = "organization", ignore = true),
    })
    ExamDto examToExamDto(Exam exam);

    @Mappings({
            @Mapping(source = "id", target = "examId"),
            @Mapping(source = "examName", target = "examName"),
            @Mapping(source = "createdAt", target = "createdAt"),
            @Mapping(source = "lastModifiedAt", target = "lastModifiedAt"),
            @Mapping(source = "organization", target = "organization", ignore = true),
    })
    List<ExamDto> examToExamDto(List<Exam> exam);

}
