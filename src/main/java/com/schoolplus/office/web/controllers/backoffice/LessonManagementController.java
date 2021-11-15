package com.schoolplus.office.web.controllers.backoffice;

import com.schoolplus.office.services.LessonService;
import com.schoolplus.office.utils.SortingUtils;
import com.schoolplus.office.web.models.CreatingLessonDto;
import com.schoolplus.office.web.models.EditingLessonDto;
import com.schoolplus.office.web.models.ErrorResponseDto;
import com.schoolplus.office.web.models.LessonDto;
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

@Tag(name = "Lesson Management Controller", description = "Exposes lesson management endpoints")
@RequiredArgsConstructor
@RequestMapping(LessonManagementController.ENDPOINT)
@RestController
public class LessonManagementController {

    public static final String ENDPOINT = "/management/lessons";

    private final LessonService lessonService;

    @Operation(summary = "Get Lessons")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lesson are listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            })
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LessonDto>> getLessons(@RequestParam(defaultValue = "0") Integer page,
                                                      @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(lessonService.getLessons(PageRequest.of(page, size)), HttpStatus.OK);
    }

    @Operation(summary = "Get Lessons By Organization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lesson are listed"),
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
    public ResponseEntity<Page<LessonDto>> getLessonsByOrganization(@Valid @PathVariable Long organizationId,
                                                                    @RequestParam(defaultValue = "0") Integer page,
                                                                    @RequestParam(defaultValue = "10") Integer size,
                                                                    @RequestParam(defaultValue = "createdAt") String sort,
                                                                    @RequestParam(defaultValue = "asc") String order,
                                                                    @RequestParam(defaultValue = "") String search) {
        return new ResponseEntity<>(lessonService
                .getLessonsByOrganization(organizationId, PageRequest.of(page, size, SortingUtils.generateSort(sort, order)), search),
                HttpStatus.OK);
    }

    @Operation(summary = "Get Lesson")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lesson info is listed"),
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
    @GetMapping(value = "/{lessonId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LessonDto> getLesson(@Valid @PathVariable Long lessonId) {
        return new ResponseEntity<>(lessonService.getLesson(lessonId), HttpStatus.OK);
    }

    @Operation(summary = "Create Lesson")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lesson is created"),
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
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LessonDto> createLesson(@Valid @RequestBody CreatingLessonDto creatingLesson) {
        return new ResponseEntity<>(lessonService.createLesson(creatingLesson), HttpStatus.CREATED);
    }

    @Operation(summary = "Edit Lesson")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lesson is updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Lesson || Organization was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @PutMapping(value = "/{lessonId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateLesson(@Valid @PathVariable Long lessonId,
                                       @Valid @RequestBody EditingLessonDto editingLesson) {
        lessonService.updateLesson(lessonId, editingLesson);
    }

    @Operation(summary = "Delete Lesson")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lesson is deleted"),
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
    @DeleteMapping(value = "/{lessonId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLesson(@Valid @PathVariable Long lessonId) {
        lessonService.deleteLesson(lessonId);
    }

}
