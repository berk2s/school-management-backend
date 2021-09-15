package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExamSkeletonDto {

    private Long examSkeletonId;

    private String examSkeletonName;

    private List<ExamFieldDto> examFields = new ArrayList<>();

    private Timestamp createdAt;

    private Timestamp lastModifiedAt;

}