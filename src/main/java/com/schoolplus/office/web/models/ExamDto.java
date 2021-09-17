package com.schoolplus.office.web.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.schoolplus.office.annotations.Logable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamDto {

    @Logable(type = LogableType.ID)
    private Long examId;

    private String examName;

    private ExamTypeDto examType;

    private ExamSkeletonDto examSkeleton;

    private OrganizationDto organization;

    private Timestamp createdAt;

    private Timestamp lastModifiedAt;

}
