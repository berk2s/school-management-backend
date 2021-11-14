package com.schoolplus.office.web.controllers.backoffice;

import com.schoolplus.office.services.GradeCategoryService;
import com.schoolplus.office.utils.SortingUtils;
import com.schoolplus.office.web.models.CreatingGradeCategoryDto;
import com.schoolplus.office.web.models.EditingGradeCategoryDto;
import com.schoolplus.office.web.models.ErrorResponseDto;
import com.schoolplus.office.web.models.GradeCategoryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "Grade Category Controller", description = "Exposes grade category management endpoints")
@RequiredArgsConstructor
@RequestMapping(GradeCategoryManagementController.ENDPOINT)
@RestController
public class GradeCategoryManagementController {

    public static final String ENDPOINT = "/management/grade/category";

    private final GradeCategoryService gradeCategoryService;

    @Operation(summary = "Get Grade Categories By Organization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Grade Category info are listed"),
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
    public ResponseEntity<Page<GradeCategoryDto>> getGradeCategoriesByOrganization(@Valid @PathVariable Long organizationId,
                                                                                   @RequestParam(defaultValue = "0") Integer page,
                                                                                   @RequestParam(defaultValue = "5") Integer size,
                                                                                   @RequestParam(defaultValue = "createdAt") String sort,
                                                                                   @RequestParam(defaultValue = "asc") String order,
                                                                                   @RequestParam(defaultValue = "") String search) {
        return new ResponseEntity<>(gradeCategoryService.getGradeCategoriesByOrganization(organizationId,
                PageRequest.of(page, size, SortingUtils.generateSort(sort, order)), search),
                HttpStatus.OK);
    }

    @Operation(summary = "Get Grade Category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Grade Category info is generated"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
            @ApiResponse(responseCode = "404", description = "Grade Category was not found",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
    })
    @GetMapping(value = "/{gradeCategoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GradeCategoryDto> getGradeCategory(@Valid @PathVariable Long gradeCategoryId) {
        return new ResponseEntity<>(gradeCategoryService.getGradeCategory(gradeCategoryId), HttpStatus.OK);
    }

    @Operation(summary = "Create Grade Category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Grade Category is created"),
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
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GradeCategoryDto> createGradeCategory(@Valid @RequestBody CreatingGradeCategoryDto creatingGradeCategoryDto) {
        return new ResponseEntity<>(gradeCategoryService.createGradeCategory(creatingGradeCategoryDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Update Grade Category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Grade Category is updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
            @ApiResponse(responseCode = "404", description = "Grade Category was not found",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
    })
    @PutMapping(value = "/{gradeCategoryId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateGradeCategory(@Valid @PathVariable Long gradeCategoryId,
                                    @Valid @RequestBody EditingGradeCategoryDto editingGradeCategoryDto) {
        gradeCategoryService.editGradeCategory(gradeCategoryId, editingGradeCategoryDto);
    }

    @Operation(summary = "Delete Grade Category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Grade Category is deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
            @ApiResponse(responseCode = "404", description = "Grade Category was not found",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
    })
    @DeleteMapping(value = "/{gradeCategoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGradeCategory(@Valid @PathVariable Long gradeCategoryId) {
        gradeCategoryService.deleteGradeCategory(gradeCategoryId);
    }

}
