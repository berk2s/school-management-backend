package com.schoolplus.office.web.controllers.backoffice;

import com.schoolplus.office.services.GradeService;
import com.schoolplus.office.utils.SortingUtils;
import com.schoolplus.office.web.models.CreatingGradeDto;
import com.schoolplus.office.web.models.EditingGradeDto;
import com.schoolplus.office.web.models.ErrorResponseDto;
import com.schoolplus.office.web.models.GradeDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "Grade Management Controller", description = "Exposes grade management endpoints")
@RequiredArgsConstructor
@RequestMapping(GradeManagementController.ENDPOINT)
@RestController
public class GradeManagementController {

    public final static String ENDPOINT = "/management/grades";

    private final GradeService gradeService;

    @Operation(summary = "Get Grades")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grades are listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            })
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GradeDto> getGrade(@RequestParam(defaultValue = "0") Integer page,
                                             @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity(gradeService.getGrades(PageRequest.of(page, size)), HttpStatus.OK);
    }

    @Operation(summary = "Get Grades By Organization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grades are listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Organization not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @GetMapping(value = "/organization/{organizationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<GradeDto>> getGradesByOrganization(@Valid @PathVariable Long organizationId,
                                                                  @RequestParam(defaultValue = "0") Integer page,
                                                                  @RequestParam(defaultValue = "10") Integer size,
                                                                  @RequestParam(defaultValue = "createdAt") String sort,
                                                                  @RequestParam(defaultValue = "asc") String order,
                                                                  @RequestParam(defaultValue = "") String search) {
        return new ResponseEntity(gradeService.getGradesByOrganization(organizationId,
                PageRequest.of(page, size, SortingUtils.generateSort(sort, order)),
                        search), HttpStatus.OK);
    }


    @Operation(summary = "Get Grade")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grade info is generated"),
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
    public ResponseEntity<GradeDto> getGrade(@Valid @PathVariable Long gradeId) {
        return new ResponseEntity(gradeService.getGrade(gradeId), HttpStatus.OK);
    }

    @Operation(summary = "Create Grade")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grade is created"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Organization || Classroom not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GradeDto> createGrade(@Valid @RequestBody CreatingGradeDto creatingGrade) {
        return new ResponseEntity(gradeService.createGrade(creatingGrade), HttpStatus.CREATED);
    }

    @Operation(summary = "Edit Grade")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grade is edited"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Organization || Classroom not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @PutMapping(value = "/{gradeId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editGrade(@Valid @PathVariable Long gradeId,
                                    @Valid @RequestBody EditingGradeDto editingGrade) {
        gradeService.editGrade(gradeId, editingGrade);
    }

    @Operation(summary = "Delete Grade")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grade is deleted"),
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
        gradeService.deleteGrade(gradeId);
    }
}
