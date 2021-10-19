package com.schoolplus.office.web.controllers.backoffice;

import com.schoolplus.office.services.PersonalHomeworkService;
import com.schoolplus.office.web.models.CreatingPersonalHomeworkDto;
import com.schoolplus.office.web.models.EditingPersonalHomeworkDto;
import com.schoolplus.office.web.models.ErrorResponseDto;
import com.schoolplus.office.web.models.PersonalHomeworkDto;
import io.swagger.v3.oas.annotations.Operation;
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
import java.util.UUID;

@Tag(name = "Personal Homework Management Controller", description = "Exposes personal homework management endpoints")
@RequiredArgsConstructor
@RequestMapping(PersonalHomeworkManagementController.ENDPOINT)
@RestController
public class PersonalHomeworkManagementController {

    public static final String ENDPOINT = "/management/personal/homeworks";

    private final PersonalHomeworkService personalHomeworkService;

    @Operation(summary = "Get Personal Homework")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Personal Homework info is listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Personal Homework was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @GetMapping(value = "/{personalHomeworkId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonalHomeworkDto> getPersonalHomework(@Valid @PathVariable Long personalHomeworkId) {
        return new ResponseEntity<>(personalHomeworkService.getPersonalHomework(personalHomeworkId),
                HttpStatus.OK);
    }

    @Operation(summary = "Get Personal Homeworks By Student")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Personal Homeworks are listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Student was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @GetMapping(value = "/student/{studentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PersonalHomeworkDto>> getPersonalHomeworkByStudent(@RequestParam(defaultValue = "0") Integer page,
                                                                                  @RequestParam(defaultValue = "10") Integer size,
                                                                                  @Valid @PathVariable UUID studentId) {
        return new ResponseEntity<>(personalHomeworkService.getPersonalHomeworkByStudent(studentId, PageRequest.of(page, size)),
                HttpStatus.OK);
    }

    @Operation(summary = "Get Personal Homeworks By Teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Personal Homeworks are listed"),
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
    public ResponseEntity<List<PersonalHomeworkDto>> getPersonalHomeworkByTeacher(@RequestParam(defaultValue = "0") Integer page,
                                                                                  @RequestParam(defaultValue = "10") Integer size,
                                                                                  @Valid @PathVariable UUID teacherId) {
        return new ResponseEntity<>(personalHomeworkService.getPersonalHomeworkByTeacher(teacherId, PageRequest.of(page, size)),
                HttpStatus.OK);
    }

    @Operation(summary = "Get Personal Homeworks By Lesson")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Personal Homeworks are listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Lesson was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @GetMapping(value = "/lesson/{lessonId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PersonalHomeworkDto>> getPersonalHomeworkByLesson(@RequestParam(defaultValue = "0") Integer page,
                                                                                 @RequestParam(defaultValue = "10") Integer size,
                                                                                 @Valid @PathVariable Long lessonId) {
        return new ResponseEntity<>(personalHomeworkService.getPersonalHomeworkByLesson(lessonId, PageRequest.of(page, size)),
                HttpStatus.OK);
    }

    @Operation(summary = "Create Personal Homework")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Personal Homework is created"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Student || Teacher || Lesson not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonalHomeworkDto> createPersonalHomework(@Valid @RequestBody CreatingPersonalHomeworkDto creatingPersonalHomework) {
        return new ResponseEntity<>(personalHomeworkService.createPersonalHomework(creatingPersonalHomework),
                HttpStatus.CREATED);
    }


    @Operation(summary = "Updated Personal Homework")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Personal Homework is updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Personal Homework || Student || Teacher || Lesson not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @PutMapping(value = "/{personalHomeworkId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonalHomeworkDto> updatedPersonalHomework(@Valid @PathVariable Long personalHomeworkId,
                                                                       @Valid @RequestBody EditingPersonalHomeworkDto editingPersonalHomework) {
        personalHomeworkService.updatePersonalHomework(personalHomeworkId, editingPersonalHomework);

        return ResponseEntity
                .status(HttpStatus.PERMANENT_REDIRECT)
                .header(HttpHeaders.LOCATION, ENDPOINT + "/" + personalHomeworkId)
                .build();
    }

    @Operation(summary = "Create Personal Homework")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Personal Homework is created"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Personal Homework not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @DeleteMapping(value = "/{personalHomeworkId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePersonalHomework(@Valid @PathVariable Long personalHomeworkId) {
        personalHomeworkService.deletePersonalHomework(personalHomeworkId);
    }


}
