package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatingExamSkeletonDto {

    @Size(min = 2, max = 99)
    @NotNull
    private String examSkeletonName;

    @NotNull
    private Long organizationId;

    @Size(min = 1, max = 99)
    private Set<CreatingExamFieldDto> fields = new HashSet<>();

}
