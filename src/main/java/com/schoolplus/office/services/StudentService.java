package com.schoolplus.office.services;

import com.schoolplus.office.web.models.CreatingStudentDto;
import com.schoolplus.office.web.models.EditStudentDto;
import com.schoolplus.office.web.models.StudentDto;

import java.util.UUID;

public interface StudentService {

    StudentDto getStudent(UUID studentId);

    StudentDto createStudent(CreatingStudentDto creatingStudent);

    void editStudent(UUID studentId, EditStudentDto editStudent);

}
