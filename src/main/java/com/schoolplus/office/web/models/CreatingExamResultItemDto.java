package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatingExamResultItemDto {

    private String studentId;

    private Long classRoomId;

    @NotNull
    private BigDecimal sortable;

    private Map<String, String> resultData = new HashMap<>();

}
