package com.schoolplus.office.services;

import com.schoolplus.office.web.models.CreatingStudentDto;
import com.schoolplus.office.web.models.EditingStudentDto;
import com.schoolplus.office.web.models.StudentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface StudentService {

    Page<StudentDto> getStudentsByOrganization(Long organizationId, Pageable pageable, String search);

    StudentDto getStudent(UUID studentId);

    StudentDto createStudent(CreatingStudentDto creatingStudent);

    void editStudent(UUID studentId, EditingStudentDto editStudent);

}
