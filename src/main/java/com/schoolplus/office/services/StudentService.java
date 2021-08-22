package com.schoolplus.office.services;

import com.schoolplus.office.web.models.CreatingStudentDto;
import com.schoolplus.office.web.models.StudentDto;

public interface StudentService {

    StudentDto createStudent(CreatingStudentDto creatingStudent);

}
