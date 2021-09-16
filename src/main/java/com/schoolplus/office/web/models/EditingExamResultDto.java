package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditingExamResultDto {

    private Long examId;

    private List<Long> removedExamResultItems = new ArrayList<>();

    private List<CreatingExamResultItemDto> addedExamResultItems = new ArrayList<>();

}
