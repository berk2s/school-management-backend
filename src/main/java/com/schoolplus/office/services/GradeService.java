package com.schoolplus.office.services;

import com.schoolplus.office.web.models.CreatingGradeDto;
import com.schoolplus.office.web.models.EditingGradeDto;
import com.schoolplus.office.web.models.GradeDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GradeService {

    List<GradeDto> getGrades(Pageable pageable);

    GradeDto getGrade(Long gradeId);

    GradeDto createGrade(CreatingGradeDto creatingGrade);

    void editGrade(Long gradeId, EditingGradeDto editingGrade);

    void deleteGrade(Long gradeId);

}
