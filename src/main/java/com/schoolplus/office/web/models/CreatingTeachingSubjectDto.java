package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatingTeachingSubjectDto {

    @NotNull
    @Size(min = 3, max = 99)
    private String subjectName;

    @Size(max = 299)
    private List<String> teachers = new ArrayList<>();

    @NotNull
    private Long organizationId;

}
