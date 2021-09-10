package com.schoolplus.office.services;

import com.schoolplus.office.web.models.CreatingPersonalHomeworkDto;
import com.schoolplus.office.web.models.EditingPersonalHomeworkDto;
import com.schoolplus.office.web.models.PersonalHomeworkDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PersonalHomeworkService {

    PersonalHomeworkDto getPersonalHomework(Long personalHomeworkId);

    List<PersonalHomeworkDto> getPersonalHomeworkByStudent(UUID studentId, Pageable pageable);

    List<PersonalHomeworkDto> getPersonalHomeworkByTeacher(UUID teacherId, Pageable pageable);

    List<PersonalHomeworkDto> getPersonalHomeworkByLesson(Long lessonId, Pageable pageable);

    PersonalHomeworkDto createPersonalHomework(CreatingPersonalHomeworkDto creatingPersonalHomework);

    void updatePersonalHomework(Long personalHomeworkId, EditingPersonalHomeworkDto editingPersonalHomework);

    void deletePersonalHomework(Long personalHomeworkId);

}
