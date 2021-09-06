package com.schoolplus.office.web.controllers.backoffice;

import com.schoolplus.office.services.OrganizationService;
import com.schoolplus.office.web.models.CreatingOrganizationDto;
import com.schoolplus.office.web.models.EditingOrganizationDto;
import com.schoolplus.office.web.models.ErrorResponseDto;
import com.schoolplus.office.web.models.OrganizationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Organization Management Controller", description = "Exposes organization management endpoints")
@RequiredArgsConstructor
@RequestMapping(OrganizationManagementController.ENDPOINT)
@RestController
public class OrganizationManagementController {

    public static final String ENDPOINT = "/management/organizations";

    private final OrganizationService organizationService;

    @Operation(summary = "Get Organizations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Organizations are lsited"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrganizationDto>> getOrganzations() {
        return new ResponseEntity<>(organizationService.getOrganizations(), HttpStatus.OK);
    }

    @Operation(summary = "Get Organization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Organization info is created"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
            @ApiResponse(responseCode = "404", description = "Organzation was not found",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
    })
    @GetMapping(value = "/{organizationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrganizationDto> getOrganzation(@Valid @PathVariable Long organizationId) {
        return new ResponseEntity<>(organizationService.getOrganization(organizationId), HttpStatus.OK);
    }

    @Operation(summary = "Create Organization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Organization is created"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrganizationDto> createOrganization(@Valid @RequestBody CreatingOrganizationDto creatingOrganization) {
        return new ResponseEntity<>(organizationService.createOrganization(creatingOrganization), HttpStatus.CREATED);
    }

    @Operation(summary = "Edit Organization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Organization is updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
            @ApiResponse(responseCode = "404", description = "Organzation was not found",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
    })
    @PutMapping(value = "/{organizationId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity editOrganization(@Valid @PathVariable Long organizationId,
                                           @Valid @RequestBody EditingOrganizationDto editingOrganization) {
        organizationService.updateOrganization(organizationId, editingOrganization);

        return ResponseEntity
                .status(HttpStatus.PERMANENT_REDIRECT)
                .header(HttpHeaders.LOCATION, ENDPOINT + "/" + organizationId)
                .build();
    }

    @Operation(summary = "Delete Organization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Organization is deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
            @ApiResponse(responseCode = "404", description = "Organzation was not found",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
    })
    @DeleteMapping(value = "/{organizationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrganization(@Valid @PathVariable Long organizationId) {
        organizationService.deleteOrganization(organizationId);
    }
}
