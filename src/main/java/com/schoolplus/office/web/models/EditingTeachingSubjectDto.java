package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditingTeachingSubjectDto {

    @Size(min = 3, max = 99)
    private String subjectName;

    private List<String> addedTeachers = new ArrayList<>();

    private List<String> removedTeachers = new ArrayList<>();

    private Long organizationId;

}
