package com.schoolplus.office.services;

import com.schoolplus.office.web.models.CreatingTeachingSubjectDto;
import com.schoolplus.office.web.models.EditingTeachingSubjectDto;
import com.schoolplus.office.web.models.TeachingSubjectDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TeachingSubjectService {

    List<TeachingSubjectDto> getTeachingSubjects(Pageable pageable);

    TeachingSubjectDto getTeachingSubject(Long teachingSubjectId);

    TeachingSubjectDto createTeachingSubject(CreatingTeachingSubjectDto creatingTeachingSubject);

    void updateTeachingSubject(Long teachingSubjectId, EditingTeachingSubjectDto editingTeachingSubject);

    void deleteTeachingSubject(Long teachingSubjectId);

}
