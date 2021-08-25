package com.schoolplus.office.web.controllers.backoffice;

import com.schoolplus.office.services.GradeService;
import com.schoolplus.office.web.models.CreatingGradeDto;
import com.schoolplus.office.web.models.EditingGradeDto;
import com.schoolplus.office.web.models.GradeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping(GradeManagementController.ENDPOINT)
@RestController
public class GradeManagementController {

    public static final String ENDPOINT = "/management/grades";

    private final GradeService gradeService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GradeDto>> getGrades(@RequestParam(defaultValue = "0") Integer page,
                                                    @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(gradeService.getGrades(PageRequest.of(page, size)), HttpStatus.OK);
    }

    @GetMapping(value = "/{gradeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GradeDto> getGrade(@Valid @PathVariable Long gradeId) {
        return new ResponseEntity<>(gradeService.getGrade(gradeId), HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GradeDto> createGrade(@Valid @RequestBody CreatingGradeDto creatingGrade) {
        return new ResponseEntity<>(gradeService.createGrade(creatingGrade), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{gradeId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity editGrade(@Valid @PathVariable Long gradeId,
                                    @RequestBody EditingGradeDto editingGrade) {
        gradeService.updateGrade(gradeId, editingGrade);

        return ResponseEntity
                .status(HttpStatus.PERMANENT_REDIRECT)
                .header(HttpHeaders.LOCATION, ENDPOINT + "/" + gradeId.toString())
                .build();
    }

    @DeleteMapping(value = "/{gradeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGrade(@Valid @PathVariable Long gradeId) {
        gradeService.deleteGrade(gradeId);
    }

}
