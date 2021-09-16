package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatingExamDto {

    @Size(min = 2, max = 99)
    @NotNull
    private String examName;

    @NotNull
    private Long examTypeId;

    @NotNull
    private Long examSkeletonId;

    @NotNull
    private Long organizationId;

}
