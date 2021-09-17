package com.schoolplus.office.web.models;

import com.schoolplus.office.annotations.Logable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExamTypeDto {

    @Logable(type = LogableType.ID)
    private Long examTypeId;

    private String examTypeName;

    private Integer numberOfQuestion;

    private Integer examDuration;

    private OrganizationDto organization;

    private Timestamp createdAt;

    private Timestamp lastModifiedAt;

}
