package com.schoolplus.office.web.controllers.backoffice;

import com.schoolplus.office.services.ParentService;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(ParentManagementController.ENDPOINT)
@RestController
public class ParentManagementController {

    public final static String ENDPOINT = "/management/parents";

    private final ParentService parentService;

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
            @ApiResponse(responseCode = "308", description = "Parent is updated",
                    headers = @Header(name = "Location", description = "Location of the edited the Parent.")),
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
    public ResponseEntity handleEditingParent(@Valid @PathVariable UUID parentId,
                                              @Valid @RequestBody EditingParentDto editingParent) {
        parentService.updateParent(parentId, editingParent);

        return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT)
                .header(HttpHeaders.LOCATION, ENDPOINT + "/" + parentId.toString())
                .build();
    }

}
