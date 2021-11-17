package com.schoolplus.office.web.controllers.backoffice;

import com.schoolplus.office.services.ClassroomService;
import com.schoolplus.office.utils.SortingUtils;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Classroom Management Controller", description = "Exposes classroom management endpoints")
@RequiredArgsConstructor
@RequestMapping(ClassroomManagementController.ENDPOINT)
@RestController
public class ClassroomManagementController {

    public static final String ENDPOINT = "/management/classrooms";

    private final ClassroomService classroomService;

    @Operation(summary = "Get Classrooms By Organization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Classrooms are listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
            @ApiResponse(responseCode = "404", description = "Organization was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @GetMapping(value = "/organization/{organizationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ClassroomDto>> getClassroomsByOrganization(@Valid @PathVariable Long organizationId,
                                                                          @RequestParam(defaultValue = "0") Integer page,
                                                                          @RequestParam(defaultValue = "10") Integer size,
                                                                          @RequestParam(defaultValue = "createdAt") String sort,
                                                                          @RequestParam(defaultValue = "asc") String order,
                                                                          @RequestParam(defaultValue = "") String search) {
        return new ResponseEntity<>(classroomService.getClassroomsByOrganization(organizationId,
                PageRequest.of(page, size, SortingUtils.generateSort(sort, order)),
                search), HttpStatus.OK);
    }

    @Operation(summary = "Get Classroom")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Classrooms info is generated"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ClassroomDto>> getClassrooms(@RequestParam(defaultValue = "0") Integer page,
                                                        @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(classroomService.getClassrooms(PageRequest.of(page, size)), HttpStatus.OK);
    }

    @Operation(summary = "Get Classroom")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Classroom info is listed"),
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
    @GetMapping(value = "/{classRoomId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClassroomDto> getClassroom(@Valid @PathVariable Long classRoomId) {
        return new ResponseEntity<>(classroomService.getClassroom(classRoomId), HttpStatus.OK);
    }

    @Operation(summary = "Create Classroom")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Classroom is created"),
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
    public ResponseEntity<ClassroomDto> createClassroom(@Valid @RequestBody CreatingClassroomDto creatingGrade) {
        return new ResponseEntity<>(classroomService.createClassroom(creatingGrade), HttpStatus.CREATED);
    }

    @Operation(summary = "Update Classroom")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "308", description = "Classroom is updated",
                    headers = @Header(name = "Location", description = "Location of the edited the Grade.")),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Classroom || Organization || Teacher || Student not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @PutMapping(value = "/{classRoomId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editClassroom(@Valid @PathVariable Long classRoomId,
                          @RequestBody EditingClassroomDto editingGrade) {
        classroomService.updateClassroom(classRoomId, editingGrade);
    }

    @Operation(summary = "Delete Classroom")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Classroom is deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Classroom not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @DeleteMapping(value = "/{classRoomId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClassroom(@Valid @PathVariable Long classRoomId) {
        classroomService.deleteClassroom(classRoomId);
    }

}
