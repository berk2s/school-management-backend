package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditingExamDto {

    @Size(max = 99)
    private String examName;

    private Long examTypeId;

    private Long examSkeletonId;

    private Long organizationId;

}
