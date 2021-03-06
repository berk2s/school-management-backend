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
public class EditingClassroomDto {

    @Size(max = 300)
    private String classRoomTag;

    private String advisorTeacher;

    private List<String> addedStudents = new ArrayList<>();

    private List<String> deletedStudents = new ArrayList<>();

    private Long organizationId;

}
