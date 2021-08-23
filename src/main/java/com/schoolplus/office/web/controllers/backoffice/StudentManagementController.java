package com.schoolplus.office.web.controllers.backoffice;

import com.schoolplus.office.services.StudentService;
import com.schoolplus.office.web.models.CreatingStudentDto;
import com.schoolplus.office.web.models.EditStudentDto;
import com.schoolplus.office.web.models.StudentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(StudentManagementController.ENDPOINT)
@RestController
public class StudentManagementController {

    public final static String ENDPOINT = "/management/students";

    private final StudentService studentService;

    @GetMapping(value = "/{studentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StudentDto> getStudent(@Valid @PathVariable UUID studentId) {
        return new ResponseEntity<>(studentService.getStudent(studentId), HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StudentDto> handleCreatingStudent(@Valid @RequestBody CreatingStudentDto creatingStudent) {
        return new ResponseEntity<>(studentService.createStudent(creatingStudent), HttpStatus.CREATED);
    }

    @PutMapping(value = {"/{studentId}"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity handleEditingStudent(@Valid @PathVariable UUID studentId,
                                               @Valid @RequestBody EditStudentDto editStudent) {
        studentService.editStudent(studentId, editStudent);

        return ResponseEntity
                .status(HttpStatus.PERMANENT_REDIRECT.value())
                .header(HttpHeaders.LOCATION, ENDPOINT + "/" + studentId)
                .build();
    }

}
