package com.schoolplus.office.web.controllers.backoffice;

import com.schoolplus.office.services.TeachingSubjectService;
import com.schoolplus.office.utils.SortingUtils;
import com.schoolplus.office.web.models.CreatingTeachingSubjectDto;
import com.schoolplus.office.web.models.EditingTeachingSubjectDto;
import com.schoolplus.office.web.models.ErrorResponseDto;
import com.schoolplus.office.web.models.TeachingSubjectDto;
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
import java.util.List;

@Tag(name = "Teaching Subject Management Controller", description = "Exposes teaching subject management endpoints")
@RequiredArgsConstructor
@RequestMapping(TeachingSubjectController.ENDPOINT)
@RestController
public class TeachingSubjectController {

    public static final String ENDPOINT = "/management/teachingsubjects";

    private final TeachingSubjectService teachingSubjectService;

    @Operation(summary = "Get Teaching Subjects")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Teaching Subject are listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TeachingSubjectDto>> getTeachingSubjects(@RequestParam(defaultValue = "0") Integer page,
                                                                        @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(teachingSubjectService.getTeachingSubjects(PageRequest.of(page, size)), HttpStatus.OK);
    }

    @Operation(summary = "Get Teaching Subjects By Organization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Teaching Subject are listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
            @ApiResponse(responseCode = "404", description = "Organization was not found",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
    })
    @GetMapping(value = "/organization/{organizationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<TeachingSubjectDto>> getTeachingSubjectsByOrganization(@Valid @PathVariable Long organizationId,
                                                                                      @RequestParam(defaultValue = "0") Integer page,
                                                                                      @RequestParam(defaultValue = "10") Integer size,
                                                                                      @RequestParam(defaultValue = "createdAt") String sort,
                                                                                      @RequestParam(defaultValue = "asc") String order,
                                                                                      @RequestParam(defaultValue = "") String search) {
        return new ResponseEntity<>(teachingSubjectService
                .getTeachingSubjectsByOrganization(organizationId,
                        PageRequest.of(page, size, SortingUtils.generateSort(sort, order)), search),
                HttpStatus.OK);
    }

    @Operation(summary = "Get Teaching Subject")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Teaching Subject info is listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
            @ApiResponse(responseCode = "404", description = "Teaching Subject was not found"),
    })
    @GetMapping(path = "/{teachingSubjectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TeachingSubjectDto> getTeachingSubject(@Valid @PathVariable Long teachingSubjectId) {
        return new ResponseEntity<>(teachingSubjectService.getTeachingSubject(teachingSubjectId), HttpStatus.OK);
    }

    @Operation(summary = "Create Teaching Subject")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Teaching Subject is created"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TeachingSubjectDto> createTeachingSubject(@Valid @RequestBody CreatingTeachingSubjectDto creatingTeachingSubject) {
        return new ResponseEntity<>(teachingSubjectService.createTeachingSubject(creatingTeachingSubject),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Update Teaching Subject")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Teaching Subject is edited"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
            @ApiResponse(responseCode = "404", description = "Teaching Subject || Teacher || Organization was not found",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
    })
    @PutMapping(path = "/{teachingSubjectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTeachingSubject(@Valid @PathVariable Long teachingSubjectId,
                                                @Valid @RequestBody EditingTeachingSubjectDto editingTeachingSubject) {
        teachingSubjectService.updateTeachingSubject(teachingSubjectId, editingTeachingSubject);
    }

    @Operation(summary = "Delete Teaching Subject")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Teaching Subject is deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
            @ApiResponse(responseCode = "404", description = "Teaching Subject || Teacher || Organization was not found",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
    })
    @DeleteMapping(path = "/{teachingSubjectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTeachingSubject(@Valid @PathVariable Long teachingSubjectId) {
        teachingSubjectService.deleteTeachingSubject(teachingSubjectId);
    }
}
