package com.schoolplus.office.web.controllers.backoffice;

import com.schoolplus.office.services.SyllabusService;
import com.schoolplus.office.web.models.CreatingSyllabusDto;
import com.schoolplus.office.web.models.EditingSyllabusDto;
import com.schoolplus.office.web.models.ErrorResponseDto;
import com.schoolplus.office.web.models.SyllabusDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Tag(name = "Syllabus Management Controller", description = "Exposes syllabus management endpoints")
@RequiredArgsConstructor
@RequestMapping(SyllabusManagementController.ENDPOINT)
@RestController
public class SyllabusManagementController {

    public static final String ENDPOINT = "/management/syllabuses";

    private final SyllabusService syllabusService;

    @Operation(summary = "Get Syllabuses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Syllabus info is listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SyllabusDto>> getSyllabuses(@RequestParam(defaultValue = "0") Integer page,
                                                           @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(syllabusService.getSyllabuses(PageRequest.of(page, size)), HttpStatus.OK);
    }

    @Operation(summary = "Get Syllabus")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Syllabus info is listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
            @ApiResponse(responseCode = "404", description = "Syllabus  was not found"),
    })
    @GetMapping(value = "/{syllabusId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SyllabusDto> getSyllabus(@Valid @PathVariable Long syllabusId) {
        return new ResponseEntity<>(syllabusService.getSyllabus(syllabusId), HttpStatus.OK);
    }

    @Operation(summary = "Get Syllabus By The Classroom")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Syllabus info is listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
            @ApiResponse(responseCode = "404", description = "Classroom was not found"),
    })
    @GetMapping(value = "/classroom/{classroomId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SyllabusDto>> getSyllabusByClassroom(@RequestParam(defaultValue = "0") Integer page,
                                                                    @RequestParam(defaultValue = "7") Integer size,
                                                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                                    @Valid @PathVariable Long classroomId) {
        return new ResponseEntity<>(syllabusService.getSyllabusByClassroom(PageRequest.of(page, size), classroomId, startDate, endDate), HttpStatus.OK);
    }

    @Operation(summary = "Get Syllabus By The Lesson")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Syllabus info is listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
            @ApiResponse(responseCode = "404", description = "Lesson was not found"),
    })
    @GetMapping(value = "/lesson/{lessonId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SyllabusDto>> getSyllabusByLesson(@RequestParam(defaultValue = "0") Integer page,
                                                                 @RequestParam(defaultValue = "7") Integer size,
                                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                                 @Valid @PathVariable Long lessonId) {
        return new ResponseEntity<>(syllabusService.getSyllabusByLesson(PageRequest.of(page, size), lessonId, startDate, endDate), HttpStatus.OK);
    }

    @Operation(summary = "Get Syllabus By The Teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Syllabus info is listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
            @ApiResponse(responseCode = "404", description = "Teacher was not found"),
    })
    @GetMapping(value = "/teacher/{teacherId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SyllabusDto>> getSyllabusByTeacher(@RequestParam(defaultValue = "0") Integer page,
                                                                  @RequestParam(defaultValue = "7") Integer size,
                                                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                                  @Valid @PathVariable UUID teacherId) {
        return new ResponseEntity<>(syllabusService.getSyllabusByTeacher(PageRequest.of(page, size), teacherId, startDate, endDate), HttpStatus.OK);
    }

    @Operation(summary = "Get Syllabus By The Organization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Syllabus info is listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
            @ApiResponse(responseCode = "404", description = "Organization was not found"),
    })
    @GetMapping(value = "/organization/{organizationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SyllabusDto>> getSyllabusByOrganization(@RequestParam(defaultValue = "0") Integer page,
                                                                  @RequestParam(defaultValue = "7") Integer size,
                                                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                                  @Valid @PathVariable Long organizationId) {
        return new ResponseEntity<>(syllabusService.getSyllabusByOrganization(PageRequest.of(page, size), organizationId, startDate, endDate), HttpStatus.OK);
    }

    @Operation(summary = "Create Syllabus")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Syllabus is created"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
            @ApiResponse(responseCode = "404", description = "Organization || Classroom || Lesson || Teacher  was not found"),
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SyllabusDto> createSyllabus(@Valid @RequestBody CreatingSyllabusDto creatingSyllabus) {
        return new ResponseEntity<>(syllabusService.createSyllabus(creatingSyllabus), HttpStatus.CREATED);
    }

    @Operation(summary = "Edit Syllabus")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Syllabus is edited"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
            @ApiResponse(responseCode = "404", description = "Syllabus || Organization || Classroom || Lesson || Teacher  was not found"),
    })
    @PutMapping(value = "/{syllabusId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createSyllabus(@Valid @PathVariable Long syllabusId,
                                                      @Valid @RequestBody EditingSyllabusDto editingSyllabus) {
        syllabusService.editSyllabus(syllabusId, editingSyllabus);

        return ResponseEntity
                .status(HttpStatus.PERMANENT_REDIRECT)
                .header(HttpHeaders.LOCATION, ENDPOINT + "/" + syllabusId)
                .build();
    }

    @Operation(summary = "Delete Syllabus")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Syllabus is deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
            @ApiResponse(responseCode = "404", description = "Syllabus was not found"),
    })
    @DeleteMapping(value = "/{syllabusId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createSyllabus(@Valid @PathVariable Long syllabusId) {
        syllabusService.deleteSyllabus(syllabusId);
    }

}
