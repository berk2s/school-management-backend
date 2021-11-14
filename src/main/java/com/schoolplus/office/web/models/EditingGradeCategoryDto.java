package com.schoolplus.office.web.models;

import lombok.Data;

import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
public class EditingGradeCategoryDto {

    @Size(max = 100)
    private String gradeCategoryName;

    private Long organizationId;

    @Size(max = 100)
    private Set<Long> addedGrades = new HashSet<>();

    @Size(max = 100)
    private Set<Long> deletedGrades = new HashSet<>();

}
