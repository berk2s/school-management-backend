package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExamResultDto {

    private Long examResultId;

    private ExamDto exam;

    private Set<ExamResultItemDto> examResultItems = new HashSet<>();

    private Timestamp createdAt;

    private Timestamp lastModifiedAt;

}
