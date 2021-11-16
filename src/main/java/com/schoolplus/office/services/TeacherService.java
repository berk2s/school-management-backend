package com.schoolplus.office.services;

import com.schoolplus.office.web.models.CreatingTeacherDto;
import com.schoolplus.office.web.models.EditingTeacherDto;
import com.schoolplus.office.web.models.TeacherDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TeacherService {

    Page<TeacherDto> getTeachersByOrganization(Long organizationId, Pageable pageable, String search);

    TeacherDto getTeacher(UUID teacherId);

    TeacherDto createTeacher(CreatingTeacherDto creatingTeacher);

    void updateTeacher(UUID teacherId, EditingTeacherDto editTeacher);

}
