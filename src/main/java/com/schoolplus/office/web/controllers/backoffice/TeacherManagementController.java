package com.schoolplus.office.web.controllers.backoffice;

import com.schoolplus.office.services.TeacherService;
import com.schoolplus.office.web.models.CreatingTeacherDto;
import com.schoolplus.office.web.models.EditingTeacherDto;
import com.schoolplus.office.web.models.ErrorResponseDto;
import com.schoolplus.office.web.models.TeacherDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Get Teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teacher info is generated"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Teacher not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @GetMapping(value = "/{teacherId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TeacherDto> getTeacher(@Valid @PathVariable UUID teacherId) {
        return new ResponseEntity<>(teacherService.getTeacher(teacherId), HttpStatus.OK);
    }

    @Operation(summary = "Create Teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Teacher is created"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Teaching Subject not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TeacherDto> handleCreating(@Valid @RequestBody CreatingTeacherDto creatingTeacher) {
        return new ResponseEntity<>(teacherService.createTeacher(creatingTeacher), HttpStatus.CREATED);
    }

    @Operation(summary = "Update Teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "308", description = "Teacher is updated",
                    headers = @Header(name = "Location", description = "Location of the edited the Teacher.")),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Teaching Subject not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @PutMapping(value = "/{teacherId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateTeacher(@Valid @PathVariable UUID teacherId,
                                        @Valid @RequestBody EditingTeacherDto editTeacher) {
        teacherService.updateTeacher(teacherId, editTeacher);

        return ResponseEntity
                .status(HttpStatus.PERMANENT_REDIRECT)
                .header(HttpHeaders.LOCATION, ENDPOINT + "/" + teacherId)
                .build();
    }

}
