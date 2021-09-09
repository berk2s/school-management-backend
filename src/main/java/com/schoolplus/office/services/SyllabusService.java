package com.schoolplus.office.services;

import com.schoolplus.office.domain.Syllabus;
import com.schoolplus.office.web.models.CreatingSyllabusDto;
import com.schoolplus.office.web.models.EditingSyllabusDto;
import com.schoolplus.office.web.models.SyllabusDto;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SyllabusService {

    List<SyllabusDto> getSyllabuses(Pageable pageable);

    SyllabusDto getSyllabus(Long syllabusId);

    List<SyllabusDto> getSyllabusByClassroom(Pageable pageable, Long classroomId, LocalDateTime startDate, LocalDateTime endDate);

    List<SyllabusDto> getSyllabusByLesson(Pageable pageable, Long lessonId, LocalDateTime startDate, LocalDateTime endDate);

    List<SyllabusDto> getSyllabusByTeacher(Pageable pageable, UUID teacherId, LocalDateTime startDate, LocalDateTime endDate);

    List<SyllabusDto> getSyllabusByOrganization(Pageable pageable, Long organizationId, LocalDateTime startDate, LocalDateTime endDate);

    SyllabusDto createSyllabus(CreatingSyllabusDto creatingSyllabus);

    void editSyllabus(Long syllabusId, EditingSyllabusDto editingSyllabus);

    void deleteSyllabus(Long syllabusId);

}
