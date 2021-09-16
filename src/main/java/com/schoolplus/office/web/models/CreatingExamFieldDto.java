package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatingExamFieldDto {

    @Size(min = 2, max = 99)
    @NotNull
    private String examFieldName;

    @NotNull
    private Boolean isReference;

    @Size(max = 99)
    private ReferenceField referenceField;

}
