package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditingExamSkeletonDto {

    @Size(max = 99)
    private String examSkeletonName;

    private Long organizationId;

    @Size(max = 99)
    private Set<CreatingExamFieldDto> addedFields = new HashSet<>();

    @Size(max = 99)
    private Set<Long> removedFields = new HashSet<>();


}
