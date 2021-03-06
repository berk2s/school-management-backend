package com.schoolplus.office.services;

import com.schoolplus.office.web.models.CreatingLessonDto;
import com.schoolplus.office.web.models.EditingLessonDto;
import com.schoolplus.office.web.models.LessonDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LessonService {

    List<LessonDto> getLessonsByOrganization(Long organizationId, Pageable pageable);

    List<LessonDto> getLessons(Pageable pageable);

    LessonDto getLesson(Long lessonId);

    LessonDto createLesson(CreatingLessonDto creatingLesson);

    void updateLesson(Long lessonId, EditingLessonDto editingLesson);

    void deleteLesson(Long lessonId);

}
