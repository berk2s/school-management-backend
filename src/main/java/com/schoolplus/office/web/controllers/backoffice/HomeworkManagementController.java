package com.schoolplus.office.web.controllers.backoffice;

import com.schoolplus.office.services.HomeworkService;
import com.schoolplus.office.web.models.CreatingHomeworkDto;
import com.schoolplus.office.web.models.EditingHomeworkDto;
import com.schoolplus.office.web.models.ErrorResponseDto;
import com.schoolplus.office.web.models.HomeworkDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping(HomeworkManagementController.ENDPOINT)
@RestController
public class HomeworkManagementController {

    public static final String ENDPOINT = "/management/homeworks";

    private final HomeworkService homeworkService;

    @Operation(summary = "Get Homeworks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Homework info is listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            })
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<HomeworkDto>> getHomeworks(@RequestParam(defaultValue = "0") Integer page,
                                                          @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(homeworkService.getHomeworks(PageRequest.of(page, size)), HttpStatus.OK);
    }

    @Operation(summary = "Get Homework")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Homework info is listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Homework was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @GetMapping(value = "/{homeworkId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HomeworkDto> getHomework(@Valid @PathVariable Long homeworkId) {
        return new ResponseEntity<>(homeworkService.getHomework(homeworkId), HttpStatus.OK);
    }

    @Operation(summary = "Get Homeworks By Classroom")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Homework info is listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Classroom was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @GetMapping(value = "/classroom/{classRoomId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<HomeworkDto>> getHomeworksByClassroom(@RequestParam(defaultValue = "0") Integer page,
                                                                     @RequestParam(defaultValue = "10") Integer size,
                                                                     @Valid @PathVariable Long classRoomId) {
        return new ResponseEntity<>(homeworkService.getHomeworksByClassroom(classRoomId, PageRequest.of(page, size)), HttpStatus.OK);
    }

    @Operation(summary = "Get Homeworks By Teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Homework info is listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Teacher was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @GetMapping(value = "/teacher/{teacherId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<HomeworkDto>> getHomeworksByTeacher(@RequestParam(defaultValue = "0") Integer page,
                                                                   @RequestParam(defaultValue = "10") Integer size,
                                                                   @Valid @PathVariable String teacherId) {
        return new ResponseEntity<>(homeworkService.getHomeworksByTeacher(teacherId, PageRequest.of(page, size)), HttpStatus.OK);
    }

    @Operation(summary = "Get Homeworks By Syllabus")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Homework info is listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Syllabus was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @GetMapping(value = "/syllabus/{syllabusId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<HomeworkDto>> getHomeworksBySyllabus(@RequestParam(defaultValue = "0") Integer page,
                                                                    @RequestParam(defaultValue = "10") Integer size,
                                                                    @Valid @PathVariable Long syllabusId) {
        return new ResponseEntity<>(homeworkService.getHomeworksBySyllabus(syllabusId, PageRequest.of(page, size)), HttpStatus.OK);
    }

    @Operation(summary = "Create Homework")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Homework is created"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Classroom || Teacher was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HomeworkDto> createHomework(@Valid @RequestBody CreatingHomeworkDto creatingHomework) {
        return new ResponseEntity<>(homeworkService.createHomework(creatingHomework), HttpStatus.CREATED);
    }

    @Operation(summary = "Update Homework")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Homework is updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Homework was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @PutMapping(value = "/{homeworkId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HomeworkDto> updateHomework(@Valid @PathVariable Long homeworkId,
                                                      @Valid @RequestBody EditingHomeworkDto editingHomework) {
        homeworkService.updateHomework(homeworkId, editingHomework);

        return ResponseEntity
                .status(HttpStatus.PERMANENT_REDIRECT)
                .header(HttpHeaders.LOCATION, ENDPOINT + "/" + homeworkId)
                .build();
    }

    @Operation(summary = "Delete Homework")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Homework is deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Homework was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @DeleteMapping(value = "/{homeworkId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHomework(@PathVariable Long homeworkId) {
        homeworkService.deleteHomework(homeworkId);
    }


}
