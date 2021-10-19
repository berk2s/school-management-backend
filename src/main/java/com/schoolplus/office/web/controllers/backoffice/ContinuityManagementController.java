package com.schoolplus.office.web.controllers.backoffice;

import com.schoolplus.office.services.ContinuityService;
import com.schoolplus.office.web.models.ContinuityDto;
import com.schoolplus.office.web.models.CreatingContinuityDto;
import com.schoolplus.office.web.models.EditingContinuityDto;
import com.schoolplus.office.web.models.ErrorResponseDto;
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

@Tag(name = "Continuity Management Controller", description = "Exposes continuity management endpoints")
@RequiredArgsConstructor
@RequestMapping(ContinuityManagementController.ENDPOINT)
@RestController
public class ContinuityManagementController {

    public final static String ENDPOINT = "/management/continuities";

    private final ContinuityService continuityService;

    @Operation(summary = "Get Continuity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Continuity info is generated"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Continuity was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @GetMapping(value = "/{continuityId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContinuityDto> getContinuity(@Valid @PathVariable UUID continuityId) {
        return new ResponseEntity<>(continuityService.getContinuity(continuityId), HttpStatus.OK);
    }

    @Operation(summary = "Get Continuity By Syllabus")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Continuity info is generated"),
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
    public ResponseEntity<List<ContinuityDto>> getContinuityBySyllabus(@RequestParam(defaultValue = "0") Integer page,
                                                                       @RequestParam(defaultValue = "10") Integer size,
                                                                       @Valid @PathVariable Long syllabusId) {
        return new ResponseEntity<>(continuityService.getContinuityBySyllabus(syllabusId, PageRequest.of(page, size)),
                HttpStatus.OK);
    }

    @Operation(summary = "Get Continuity By Classroom")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Continuity info is generated"),
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
    public ResponseEntity<List<ContinuityDto>> getContinuityByClassroom(@RequestParam(defaultValue = "0") Integer page,
                                                                        @RequestParam(defaultValue = "10") Integer size,
                                                                        @Valid @PathVariable Long classRoomId) {
        return new ResponseEntity<>(continuityService.getContinuityByClassroom(classRoomId, PageRequest.of(page, size)),
                HttpStatus.OK);
    }

    @Operation(summary = "Get Continuity By Student")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Continuity info is generated"),
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
    public ResponseEntity<List<ContinuityDto>> getContinuityByStudent(@RequestParam(defaultValue = "0") Integer page,
                                                                      @RequestParam(defaultValue = "10") Integer size,
                                                                      @Valid @PathVariable UUID studentId) {
        return new ResponseEntity<>(continuityService.getContinuityByStudent(studentId, PageRequest.of(page, size)),
                HttpStatus.OK);
    }

    @Operation(summary = "Get Continuity By Organization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Continuity info is generated"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Organization was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @GetMapping(value = "/organization/{organizationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ContinuityDto>> getContinuityByOrganization(@RequestParam(defaultValue = "0") Integer page,
                                                                      @RequestParam(defaultValue = "10") Integer size,
                                                                      @Valid @PathVariable Long organizationId) {
        return new ResponseEntity<>(continuityService.getContinuityByOrganization(organizationId, PageRequest.of(page, size)),
                HttpStatus.OK);
    }

    @Operation(summary = "Create Continuity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Continuity is created"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Classroom || Syllabus || Student || Organization was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContinuityDto> createContinuity(@Valid @RequestBody CreatingContinuityDto creatingContinuity) {
        return new ResponseEntity<>(continuityService.createContinuity(creatingContinuity), HttpStatus.CREATED);
    }

    @Operation(summary = "Edit Continuity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Continuity is edited"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Continuity || Classroom || Syllabus || Student || Organization was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @PutMapping(path = "/{continuityId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity editContinuity(@Valid @PathVariable UUID continuityId,
                                         @Valid @RequestBody EditingContinuityDto editingContinuity) {
        continuityService.editContinuity(continuityId, editingContinuity);

        return ResponseEntity
                .status(HttpStatus.PERMANENT_REDIRECT)
                .header(HttpHeaders.LOCATION, ENDPOINT + "/" + continuityId)
                .build();
    }

    @Operation(summary = "Delete Continuity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Continuity is deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Continuity was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @DeleteMapping(path = "/{continuityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteContinuity(@Valid @PathVariable UUID continuityId) {
        continuityService.deleteContinuity(continuityId);
    }

}
