package com.schoolplus.office.web.controllers.backoffice;

import com.schoolplus.office.services.ExamService;
import com.schoolplus.office.web.models.*;
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
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping(ExamManagementController.ENDPOINT)
@RestController
public class ExamManagementController {

    public final static String ENDPOINT = "/management/exams";

    public final ExamService examService;

    @Operation(summary = "Get Exams")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam are listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            })
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ExamDto>> getExams(@RequestParam(defaultValue = "0") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(examService.getExams(PageRequest.of(page, size)), HttpStatus.OK);
    }

    @Operation(summary = "Get Exams By Organization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam are listed"),
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
    public ResponseEntity<List<ExamDto>> getExamsByOrganization(@Valid @PathVariable Long organizationId,
                                                                @RequestParam(defaultValue = "0") Integer page,
                                                                @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(examService.getExamsByOrganization(organizationId, PageRequest.of(page, size)), HttpStatus.OK);
    }


    @Operation(summary = "Get Exam")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam info is generated"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Exam was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @GetMapping(value = "/{examId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExamDto> getExam(@Valid @PathVariable Long examId) {
        return new ResponseEntity<>(examService.getExam(examId), HttpStatus.OK);
    }

    @Operation(summary = "Get Exam Types")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam Types are listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            })
    })
    @GetMapping(value = "/types", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ExamTypeDto>> getExamTypes(@RequestParam(defaultValue = "0") Integer page,
                                                          @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(examService.getExamTypes(PageRequest.of(page, size)), HttpStatus.OK);
    }

    @Operation(summary = "Get Exam Types By Organization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam Types are listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Organization was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            })
    })
    @GetMapping(value = "/types/organization/{organizationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ExamTypeDto>> getExamTypesByOrganization(@Valid @PathVariable Long organizationId,
                                                                        @RequestParam(defaultValue = "0") Integer page,
                                                                        @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(examService.getExamTypesByOrganization(organizationId, PageRequest.of(page, size)), HttpStatus.OK);
    }

    @Operation(summary = "Get Exam Type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam Type info is generated"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Exam Type was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @GetMapping(value = "/types/{examTypeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExamTypeDto> getExamType(@Valid @PathVariable Long examTypeId) {
        return new ResponseEntity<>(examService.getExamType(examTypeId), HttpStatus.OK);
    }

    @Operation(summary = "Get Exam Skeletons")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam Skeletons are listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            })
    })
    @GetMapping(value = "/skeletons", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ExamSkeletonDto>> getExamSkeletons(@RequestParam(defaultValue = "0") Integer page,
                                                                  @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(examService.getExamSkeletons(PageRequest.of(page, size)), HttpStatus.OK);
    }

    @Operation(summary = "Get Exam Skeletons By Organization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam Types are listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Organization was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            })
    })
    @GetMapping(value = "/skeletons/organization/{organizationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ExamSkeletonDto>> getExamSkeletonsByOrganization(@Valid @PathVariable Long organizationId,
                                                                                @RequestParam(defaultValue = "0") Integer page,
                                                                                @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(examService.getExamSkeletonsByOrganization(organizationId, PageRequest.of(page, size)),
                HttpStatus.OK);
    }

    @Operation(summary = "Get Exam Skeleton")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam Skeleton info is generated"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Exam Skeleton was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @GetMapping(value = "/skeletons/{examSkeletonId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExamSkeletonDto> getExamSkeleton(@Valid @PathVariable Long examSkeletonId) {
        return new ResponseEntity<>(examService.getExamSkeleton(examSkeletonId), HttpStatus.OK);
    }

    @Operation(summary = "Get Exam Result")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam Result info is generated"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Exam Result was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @GetMapping(value = "/results/{examResultId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExamResultDto> getExamResult(@Valid @PathVariable Long examResultId) {
        return new ResponseEntity<>(examService.getExamResult(examResultId), HttpStatus.OK);
    }

    @Operation(summary = "Get Exam Results")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam Result are listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            })
    })
    @GetMapping(value = "/results", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ExamResultDto>> getExamResults(@RequestParam(defaultValue = "0") Integer page,
                                                              @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(examService.getExamResults(PageRequest.of(page, size)), HttpStatus.OK);
    }

    @Operation(summary = "Get Exam Results By Organization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam Result are listed"),
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
    @GetMapping(value = "/results/organization/{organizationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ExamResultDto>> getExamResultsByOrganization(@Valid @PathVariable Long organizationId,
                                                                            @RequestParam(defaultValue = "0") Integer page,
                                                                            @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(examService.getExamResultsByOrganization(organizationId, PageRequest.of(page, size)), HttpStatus.OK);
    }

    @Operation(summary = "Get Exam Results By Student")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam Result are listed"),
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
    @GetMapping(value = "/results/student/{studentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ExamResultDto>> getExamResultsByStudent(@Valid @PathVariable UUID studentId,
                                                                       @RequestParam(defaultValue = "0") Integer page,
                                                                       @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(examService.getExamResultsByStudent(studentId, PageRequest.of(page, size)), HttpStatus.OK);
    }

    @Operation(summary = "Get Exam Results By Classroom")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam Result are listed"),
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
    @GetMapping(value = "/results/classroom/{classRoomId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ExamResultDto>> getExamResultsByClassroom(@Valid @PathVariable Long classRoomId,
                                                                       @RequestParam(defaultValue = "0") Integer page,
                                                                         @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(examService.getExamResultsByClassroom(classRoomId, PageRequest.of(page, size)), HttpStatus.OK);
    }

    @Operation(summary = "Get Exam Results By Grade")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam Result are listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Grade was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @GetMapping(value = "/results/classroom/grade/{gradeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ExamResultDto>> getExamResultsByGrade(@Valid @PathVariable Long gradeId,
                                                                         @RequestParam(defaultValue = "0") Integer page,
                                                                         @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(examService.getExamResultsByGrade(gradeId, PageRequest.of(page, size)), HttpStatus.OK);
    }

    @Operation(summary = "Get Exam Result Item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam Result Item info is generated"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Exam Result Item was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @GetMapping(value = "/results/item/{examResultItemId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExamResultItemDto> getExamResultItem(@Valid @PathVariable Long examResultItemId) {
        return new ResponseEntity<>(examService.getExamResultItem(examResultItemId), HttpStatus.OK);
    }

    @Operation(summary = "Create Exam")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam is created"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Organization || Exam Type || Exam Skeleton was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExamDto> createExam(@Valid @RequestBody CreatingExamDto creatingExam) {
        return new ResponseEntity<>(examService.createExam(creatingExam), HttpStatus.CREATED);
    }

    @Operation(summary = "Create Exam Type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam Type is created"),
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
    @PostMapping(value = "/types", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExamTypeDto> createExamType(@Valid @RequestBody CreatingExamTypeDto creatingExamType) {
        return new ResponseEntity<>(examService.createExamType(creatingExamType), HttpStatus.CREATED);
    }

    @Operation(summary = "Create Exam Skeleton")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam Skeleton is created"),
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
    @PostMapping(value = "/skeletons", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExamSkeletonDto> createExamSkeleton(@Valid @RequestBody CreatingExamSkeletonDto creatingExamSkeleton) {
        return new ResponseEntity<>(examService.createExamSkeleton(creatingExamSkeleton), HttpStatus.CREATED);
    }

    @Operation(summary = "Create Exam Result")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam Result is created"),
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
    @PostMapping(value = "/results/exam/{examId}", consumes = "application/vnd.ms-excel", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createExamResult(@Valid @PathVariable Long examId,
                                           @Valid @RequestParam("result") MultipartFile result) {
        return new ResponseEntity<>(examService.createExamResult(examId, result), HttpStatus.CREATED);
    }

    @Operation(summary = "Update Exam")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam is updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Organization || Exam Type || Exam Skeleton was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @PutMapping(value = "/{examId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateExam(@Valid @PathVariable Long examId,
                                     @Valid @RequestBody EditingExamDto editingExam) {
        examService.updateExam(examId, editingExam);

        return ResponseEntity
                .status(HttpStatus.PERMANENT_REDIRECT)
                .header(HttpHeaders.LOCATION, ENDPOINT + "/" + examId)
                .build();
    }

    @Operation(summary = "Update Exam Type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam Type is updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Organization || Exam Type was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @PutMapping(value = "/types/{examTypeId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateExamType(@Valid @PathVariable Long examTypeId,
                                         @Valid @RequestBody EditingExamTypeDto editingExamType) {
        examService.updateExamType(examTypeId, editingExamType);

        return ResponseEntity
                .status(HttpStatus.PERMANENT_REDIRECT)
                .header(HttpHeaders.LOCATION, ENDPOINT + "/types/" + examTypeId)
                .build();
    }

    @Operation(summary = "Update Exam Skeleton")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam is updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Organization || Exam Skeleton || Exam Field was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @PutMapping(value = "/skeletons/{examSkeletonId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateExamSkeleton(@Valid @PathVariable Long examSkeletonId,
                                             @Valid @RequestBody EditingExamSkeletonDto editingExamSkeleton) {
        examService.updateExamSkeleton(examSkeletonId, editingExamSkeleton);

        return ResponseEntity
                .status(HttpStatus.PERMANENT_REDIRECT)
                .header(HttpHeaders.LOCATION, ENDPOINT + "/skeletons/" + examSkeletonId)
                .build();
    }

    @Operation(summary = "Update Exam Result")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam Result is updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Exam || Exam Result was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @PutMapping(value = "/results/{examResultId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateExamResult(@Valid @PathVariable Long examResultId,
                                             @Valid @RequestBody EditingExamResultDto editingExamResult) {
        examService.updateExamResult(examResultId, editingExamResult);

        return ResponseEntity
                .status(HttpStatus.PERMANENT_REDIRECT)
                .header(HttpHeaders.LOCATION, ENDPOINT + "/results/" + examResultId)
                .build();
    }

    @Operation(summary = "Update Exam Result Item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam Result Item is updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Exam Result Item || Classroom || Student was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @PutMapping(value = "/results/item/{examResultItemId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateExamResultItem(@Valid @PathVariable Long examResultItemId,
                                           @Valid @RequestBody EditingExamResultItemDto editingExamResultItem) {
        examService.updateExamResultItem(examResultItemId, editingExamResultItem);

        return ResponseEntity
                .status(HttpStatus.PERMANENT_REDIRECT)
                .header(HttpHeaders.LOCATION, ENDPOINT + "/results/item/" + examResultItemId)
                .build();
    }

    @Operation(summary = "Delete Exam")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam is deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Exam was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @DeleteMapping(value = "/{examId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExam(@Valid @PathVariable Long examId) {
        examService.deleteExam(examId);
    }

    @Operation(summary = "Delete Exam Type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam Type is deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Exam Type was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @DeleteMapping(value = "/types/{examTypeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExamType(@Valid @PathVariable Long examTypeId) {
        examService.deleteExamType(examTypeId);
    }

    @Operation(summary = "Delete Exam Skeleton")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam Skeleton is deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Exam Skeleton was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @DeleteMapping(value = "/skeletons/{examSkeletonId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExamSkeleton(@Valid @PathVariable Long examSkeletonId) {
        examService.deleteExamSkeleton(examSkeletonId);
    }

    @Operation(summary = "Delete Exam Result")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam Result is deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Exam Result was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @DeleteMapping(value = "/results/{examResultId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExamResult(@Valid @PathVariable Long examResultId) {
        examService.deleteExamResult(examResultId);
    }

}
