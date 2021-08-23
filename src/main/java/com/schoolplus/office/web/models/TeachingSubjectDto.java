package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TeachingSubjectDto {

    private Long teachingSubjectId;

    private String subjectName;

    private List<TeacherDto> teachers = new ArrayList<>();

}
