package com.schoolplus.office.web.models;

import com.schoolplus.office.domain.Organization;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatingExamTypeDto {

    @Size(min = 2, max = 99)
    @NotNull
    private String examTypeName;

    @NotNull
    private Integer numberOfQuestion;

    @NotNull
    private Integer examDuration;

    @NotNull
    private Long organizationId;

}
