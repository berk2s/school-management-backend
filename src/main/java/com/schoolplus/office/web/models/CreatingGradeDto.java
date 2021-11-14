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
public class CreatingGradeDto {

    @Size(min = 1, max = 100)
    @NotNull
    private String gradeName;

    @Size(max = 99)
    private List<Long> classRooms = new ArrayList<>();

    @NotNull
    private Long gradeCategoryId;

    @NotNull
    private Long organizationId;

}
