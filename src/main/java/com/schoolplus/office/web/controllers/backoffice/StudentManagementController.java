package com.schoolplus.office.web.controllers.backoffice;

import com.schoolplus.office.services.StudentService;
import com.schoolplus.office.utils.SortingUtils;
import com.schoolplus.office.web.models.CreatingStudentDto;
import com.schoolplus.office.web.models.EditingStudentDto;
import com.schoolplus.office.web.models.ErrorResponseDto;
import com.schoolplus.office.web.models.StudentDto;
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
import java.util.UUID;

@RequiredArgsConstructor
@Tag(name = "Student Management Controller", description = "Exposes student management endpoints")
@RequestMapping(StudentManagementController.ENDPOINT)
@RestController
public class StudentManagementController {

    public final static String ENDPOINT = "/management/students";

    private final StudentService studentService;

    @Operation(summary = "Get Students By Organization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Students are listed"),
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
    public ResponseEntity<Page<StudentDto>> getStudentsByOrganization(@Valid @PathVariable Long organizationId,
                                                                      @RequestParam(defaultValue = "0") Integer page,
                                                                      @RequestParam(defaultValue = "5") Integer size,
                                                                      @RequestParam(defaultValue = "createdAt") String sort,
                                                                      @RequestParam(defaultValue = "asc") String order,
                                                                      @RequestParam(defaultValue = "") String search) {
        return new ResponseEntity<>(studentService.getStudentsByOrganization(
                organizationId,
                PageRequest.of(page, size, SortingUtils.generateSort(sort, order)),
                search),
                HttpStatus.OK);
    }

    @Operation(summary = "Get Student")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student info is generated"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Student not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @GetMapping(value = "/{studentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StudentDto> getStudent(@Valid @PathVariable UUID studentId) {
        return new ResponseEntity<>(studentService.getStudent(studentId), HttpStatus.OK);
    }

    @Operation(summary = "Create Student")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Student is created"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Grade || Parent || Role || Authority  not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StudentDto> handleCreatingStudent(@Valid @RequestBody CreatingStudentDto creatingStudent) {
        return new ResponseEntity<>(studentService.createStudent(creatingStudent), HttpStatus.CREATED);
    }

    @Operation(summary = "Update Student")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "308", description = "Student is updated",
                    headers = @Header(name = "Location", description = "Location of the edited the Student.")),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Grade || Parent || Role || Authority  not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @PutMapping(value = {"/{studentId}"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity handleEditingStudent(@Valid @PathVariable UUID studentId,
                                               @Valid @RequestBody EditingStudentDto editStudent) {
        studentService.editStudent(studentId, editStudent);

        return ResponseEntity
                .status(HttpStatus.PERMANENT_REDIRECT)
                .header(HttpHeaders.LOCATION, ENDPOINT + "/" + studentId)
                .build();
    }

}
