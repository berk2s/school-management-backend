package com.schoolplus.office.services;

import com.schoolplus.office.domain.Homework;
import com.schoolplus.office.web.models.CreatingHomeworkDto;
import com.schoolplus.office.web.models.EditingHomeworkDto;
import com.schoolplus.office.web.models.HomeworkDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HomeworkService {

    List<HomeworkDto> getHomeworks(Pageable pageable);

    List<HomeworkDto> getHomeworksByClassroom(Long classroomId, Pageable pageable);

    List<HomeworkDto> getHomeworksByTeacher(String teacherId, Pageable pageable);

    List<HomeworkDto> getHomeworksBySyllabus(Long syllabusId, Pageable pageable);

    HomeworkDto getHomework(Long homeworkId);

    HomeworkDto createHomework(CreatingHomeworkDto creatingHomework);

    void updateHomework(Long homeworkId, EditingHomeworkDto editingHomework);

    void deleteHomework(Long homeworkId);

}
