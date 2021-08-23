package com.schoolplus.office.services;

import com.schoolplus.office.web.models.CreatingTeacherDto;
import com.schoolplus.office.web.models.EditTeacherDto;
import com.schoolplus.office.web.models.TeacherDto;

import java.util.UUID;

public interface TeacherService {

    TeacherDto getTeacher(UUID teacherId);

    TeacherDto createTeacher(CreatingTeacherDto creatingTeacher);

    void updateTeacher(UUID teacherId, EditTeacherDto editTeacher);

}
