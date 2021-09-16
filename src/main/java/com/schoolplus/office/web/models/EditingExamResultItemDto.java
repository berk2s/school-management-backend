package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditingExamResultItemDto {

    private Long examResultId;

    private Long classRoomId;

    private String studentId;

    private BigDecimal sortable;

    private Map<String, String> resultData = new HashMap<>();

}
