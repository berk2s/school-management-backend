package com.schoolplus.office.services;

import com.schoolplus.office.web.models.CreatingTeachingSubjectDto;
import com.schoolplus.office.web.models.EditingTeachingSubjectDto;
import com.schoolplus.office.web.models.TeachingSubjectDto;

public interface TeachingSubjectService {

    TeachingSubjectDto getTeachingSubject(Long teachingSubjectId);

    TeachingSubjectDto createTeachingSubject(CreatingTeachingSubjectDto creatingTeachingSubject);

    void updateTeachingSubject(Long teachingSubjectId, EditingTeachingSubjectDto editingTeachingSubject);

    void deleteTeachingSubject(Long teachingSubjectId);

}
