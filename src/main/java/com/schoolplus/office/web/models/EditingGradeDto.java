package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditingGradeDto {

    @Size(min = 1, max = 99)
    private String gradeName;

    @Size(max = 99)
    private List<Long> addedClassrooms = new ArrayList<>();

    @Size(max = 99)
    private List<Long> removedClassrooms = new ArrayList<>();

    private Long newGradeCategoryId;

    private Long organizationId;

}
