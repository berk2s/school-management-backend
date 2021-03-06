package com.schoolplus.office.web.controllers.backoffice;

import com.schoolplus.office.services.ClassroomService;
import com.schoolplus.office.web.models.CreatingClassroomDto;
import com.schoolplus.office.web.models.EditingClassroomDto;
import com.schoolplus.office.web.models.ErrorResponseDto;
import com.schoolplus.office.web.models.ClassroomDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Grade Management Controller", description = "Exposes student management endpoints")
@RequiredArgsConstructor
@RequestMapping(ClassroomManagementController.ENDPOINT)
@RestController
public class ClassroomManagementController {

    public static final String ENDPOINT = "/management/classrooms";

    private final ClassroomService classroomService;

    @Operation(summary = "Get Grades")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grades are listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ClassroomDto>> getGrades(@RequestParam(defaultValue = "0") Integer page,
                                                        @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(classroomService.getClassrooms(PageRequest.of(page, size)), HttpStatus.OK);
    }

    @Operation(summary = "Get Grade")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grade info is listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Grade not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @GetMapping(value = "/{gradeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClassroomDto> getGrade(@Valid @PathVariable Long gradeId) {
        return new ResponseEntity<>(classroomService.getClassroom(gradeId), HttpStatus.OK);
    }

    @Operation(summary = "Create Grade")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Grade is created"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Grade || Teacher || Student not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClassroomDto> createGrade(@Valid @RequestBody CreatingClassroomDto creatingGrade) {
        return new ResponseEntity<>(classroomService.createClassroom(creatingGrade), HttpStatus.CREATED);
    }

    @Operation(summary = "Update Grade")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "308", description = "Grade is updated",
                    headers = @Header(name = "Location", description = "Location of the edited the Grade.")),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Grade || Teacher || Student not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @PutMapping(value = "/{gradeId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity editGrade(@Valid @PathVariable Long gradeId,
                                    @RequestBody EditingClassroomDto editingGrade) {
        classroomService.updateClassroom(gradeId, editingGrade);

        return ResponseEntity
                .status(HttpStatus.PERMANENT_REDIRECT)
                .header(HttpHeaders.LOCATION, ENDPOINT + "/" + gradeId.toString())
                .build();
    }

    @Operation(summary = "Delete Grade")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Grade is deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Grade not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @DeleteMapping(value = "/{gradeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGrade(@Valid @PathVariable Long gradeId) {
        classroomService.deleteClassroom(gradeId);
    }

}
