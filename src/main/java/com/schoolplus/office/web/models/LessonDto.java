package com.schoolplus.office.web.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.schoolplus.office.annotations.Logable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LessonDto {

    @Logable(type = LogableType.ID)
    private Long lessonId;

    private String lessonName;

    private OrganizationDto organization;

    private Timestamp createdAt;

    private Timestamp lastModifiedAt;

}
