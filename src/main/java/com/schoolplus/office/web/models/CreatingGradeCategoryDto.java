package com.schoolplus.office.web.models;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CreatingGradeCategoryDto {

    @Size(min = 1, max = 100)
    @NotNull
    private String gradeCategoryName;

    @NotNull
    private Long organizationId;

}
