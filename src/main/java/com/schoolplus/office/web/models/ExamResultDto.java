package com.schoolplus.office.web.models;

import com.schoolplus.office.annotations.Logable;
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

    @Logable(type = LogableType.ID)
    private Long examResultId;

    private ExamDto exam;

    private Set<ExamResultItemDto> examResultItems = new HashSet<>();

    private Timestamp createdAt;

    private Timestamp lastModifiedAt;

}
