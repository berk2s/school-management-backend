package com.schoolplus.office.web.controllers.backoffice;

import com.schoolplus.office.services.StudentService;
import com.schoolplus.office.web.models.CreatingStudentDto;
import com.schoolplus.office.web.models.StudentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(StudentManagementController.ENDPOINT)
@RestController
public class StudentManagementController {

    public final static String ENDPOINT = "/management/students";

    private final StudentService studentService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StudentDto> handleCreate(@Valid @RequestBody CreatingStudentDto creatingStudent) {
        return new ResponseEntity<>(studentService.createStudent(creatingStudent), HttpStatus.CREATED);
    }

}
