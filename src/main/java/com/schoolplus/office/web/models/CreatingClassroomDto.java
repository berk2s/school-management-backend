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
public class CreatingClassroomDto {

    @Size(max = 300)
    private String classRoomId;

    @NotNull
    private String advisorTeacher;

    @NotNull
    private Long organizationId;

    @NotNull
    private Long gradeId;

    private List<String> students = new ArrayList<>();

}
