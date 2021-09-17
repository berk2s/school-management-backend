package com.schoolplus.office.web.models;

import com.schoolplus.office.annotations.Logable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExamResultItemDto {

    @Logable(type = LogableType.ID)
    private Long examResultItemId;

    private StudentDto student;

    private Map<String, String> resultData = new HashMap<>();

    private Timestamp createdAt;

    private Timestamp lastModifiedAt;

}
