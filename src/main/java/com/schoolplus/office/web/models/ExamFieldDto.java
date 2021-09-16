package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExamFieldDto {

    private Long examFieldId;

    private String examFieldName;

    private Boolean isReference;

    private ReferenceField referenceField;

    private ExamSkeletonDto examSkeleton;

    private Timestamp createdAt;

    private Timestamp lastModifiedAt;

}