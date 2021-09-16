package com.schoolplus.office.web.models;

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

    private Long examResultItemId;

    private StudentDto student;

    private Map<String, String> resultData = new HashMap<>();

    private Timestamp createdAt;

    private Timestamp lastModifiedAt;

}
