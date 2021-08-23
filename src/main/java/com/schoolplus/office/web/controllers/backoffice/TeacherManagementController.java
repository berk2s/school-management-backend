package com.schoolplus.office.web.controllers.backoffice;

import com.schoolplus.office.services.TeacherService;
import com.schoolplus.office.web.models.CreatingTeacherDto;
import com.schoolplus.office.web.models.EditTeacherDto;
import com.schoolplus.office.web.models.TeacherDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping(TeacherManagementController.ENDPOINT)
@RestController
public class TeacherManagementController {

    public final static String ENDPOINT = "/management/teachers";

    private final TeacherService teacherService;

    @GetMapping(value = "/{teacherId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TeacherDto> getTeacher(@Valid @PathVariable UUID teacherId) {
        return new ResponseEntity<>(teacherService.getTeacher(teacherId), HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TeacherDto> handleCreating(@Valid @RequestBody CreatingTeacherDto creatingTeacher) {
        return new ResponseEntity<>(teacherService.createTeacher(creatingTeacher), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{teacherId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateTeacher(@Valid @PathVariable UUID teacherId,
                                        @Valid @RequestBody EditTeacherDto editTeacher) {
        teacherService.updateTeacher(teacherId, editTeacher);

        return ResponseEntity
                .status(HttpStatus.PERMANENT_REDIRECT)
                .header(HttpHeaders.LOCATION, ENDPOINT + "/" + teacherId)
                .build();
    }

}
