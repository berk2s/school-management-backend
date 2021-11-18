package com.schoolplus.office.web.controllers.backoffice;

import com.schoolplus.office.services.ParentService;
import com.schoolplus.office.utils.SortingUtils;
import com.schoolplus.office.web.models.CreatingParentDto;
import com.schoolplus.office.web.models.EditingParentDto;
import com.schoolplus.office.web.models.ErrorResponseDto;
import com.schoolplus.office.web.models.ParentDto;
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
@Tag(name = "Parent Management Controller", description = "Exposes parent management endpoints")
@RequestMapping(ParentManagementController.ENDPOINT)
@RestController
public class ParentManagementController {

    public final static String ENDPOINT = "/management/parents";

    private final ParentService parentService;

    @Operation(summary = "Get Parents By Organization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parents are listed"),
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
    public ResponseEntity<Page<ParentDto>> getParent(@Valid @PathVariable Long organizationId,
                                                     @RequestParam(defaultValue = "0") Integer page,
                                                     @RequestParam(defaultValue = "10") Integer size,
                                                     @RequestParam(defaultValue = "createdAt") String sort,
                                                     @RequestParam(defaultValue = "desc") String order,
                                                     @RequestParam(defaultValue = "") String search) {

        return new ResponseEntity<>(parentService.getParentsByOrganization(organizationId,
                PageRequest.of(page, size, SortingUtils.generateSort(sort, order)), search), HttpStatus.OK);
    }

    @Operation(summary = "Get Parent")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parent info is generated"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Parent not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @GetMapping(value = "/{parentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ParentDto> getParent(@Valid @PathVariable UUID parentId) {
        return new ResponseEntity<>(parentService.getParent(parentId), HttpStatus.OK);
    }

    @Operation(summary = "Create Parent")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Parent is created"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Student not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ParentDto> handleCreatingParent(@Valid @RequestBody CreatingParentDto creatingParent) {
        return new ResponseEntity<>(parentService.createParent(creatingParent), HttpStatus.CREATED);
    }

    @Operation(summary = "Update Parent")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "308", description = "Parent is updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Parent || Student not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @PutMapping(value = "/{parentId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handleEditingParent(@Valid @PathVariable UUID parentId,
                                              @Valid @RequestBody EditingParentDto editingParent) {
        parentService.updateParent(parentId, editingParent);
    }

    @Operation(summary = "Delete Parent")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "308", description = "Parent is deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Parent || Student not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @DeleteMapping(value = "/{parentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteParent(@Valid @PathVariable UUID parentId) {
        parentService.deleteParent(parentId);
    }
}
