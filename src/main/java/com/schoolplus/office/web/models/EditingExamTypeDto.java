package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditingExamTypeDto {

    @Size(max = 99)
    private String examTypeName;

    private Integer numberOfQuestion;

    private Integer examDuration;

    private Long organizationId;

}
