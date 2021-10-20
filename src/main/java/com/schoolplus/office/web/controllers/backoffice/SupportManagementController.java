package com.schoolplus.office.web.controllers.backoffice;

import com.schoolplus.office.services.SupportService;
import com.schoolplus.office.web.models.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Tag(name = "Support Management Controller", description = "Exposes support management endpoints")
@RequiredArgsConstructor
@RequestMapping(SupportManagementController.ENDPOINT)
@RestController
public class SupportManagementController {

    public static final String ENDPOINT = "/management/supports";

    private final SupportService supportService;

    @Operation(summary = "Get Support Request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Support Request info is generated"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
            @ApiResponse(responseCode = "400", description = "Support was not found",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
    })
    @GetMapping(value = "/{supportRequestId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SupportRequestDto> getSupportRequest(@Valid @PathVariable Long supportRequestId) {
        return new ResponseEntity<>(supportService.getSupportRequest(supportRequestId), HttpStatus.OK);
    }

    @Operation(summary = "Get Support Request By Organization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Support Request are listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
            @ApiResponse(responseCode = "400", description = "Organization was not found",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
    })
    @GetMapping(value = "/organization/{organizationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SupportRequestDto>> getSupportRequestByOrganization(@Valid @PathVariable Long organizationId,
                                                                                   @RequestParam(defaultValue = "0") Integer page,
                                                                                   @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(supportService.getSupportRequestByOrganization(organizationId, PageRequest.of(page, size)), HttpStatus.OK);
    }

    @Operation(summary = "Get Support Request By Organization And Unanswered")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Support Request are listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
            @ApiResponse(responseCode = "400", description = "Organization was not found",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
    })
    @GetMapping(value = "/organization/{organizationId}/unanswered", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SupportRequestDto>> getSupportRequestByOrganizationAndUnanswered(@Valid @PathVariable Long organizationId,
                                                                                   @RequestParam(defaultValue = "0") Integer page,
                                                                                   @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(supportService.getSupportRequestByOrganizationAndUnanswered(organizationId, PageRequest.of(page, size)), HttpStatus.OK);
    }

    @Operation(summary = "Get Support Request By Organization And Answered")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Support Request are listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
            @ApiResponse(responseCode = "400", description = "Organization was not found",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
    })
    @GetMapping(value = "/organization/{organizationId}/answered", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SupportRequestDto>> getSupportRequestByOrganizationAndAnswered(@Valid @PathVariable Long organizationId,
                                                                                                @RequestParam(defaultValue = "0") Integer page,
                                                                                                @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(supportService.getSupportRequestByOrganizationAndAnswered(organizationId, PageRequest.of(page, size)), HttpStatus.OK);
    }

    @Operation(summary = "Get Support Request By Organization And Anonymous")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Support Request are listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
            @ApiResponse(responseCode = "400", description = "Organization was not found",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
    })
    @GetMapping(value = "/organization/{organizationId}/anonymous", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SupportRequestDto>> getSupportRequestByOrganizationAndAnonymous(@Valid @PathVariable Long organizationId,
                                                                                              @RequestParam(defaultValue = "0") Integer page,
                                                                                              @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(supportService.getSupportRequestByOrganizationAndAnonymous(organizationId, PageRequest.of(page, size)), HttpStatus.OK);
    }

    @Operation(summary = "Get Support Request By Organization And Named")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Support Request are listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
            @ApiResponse(responseCode = "400", description = "Organization was not found",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
    })
    @GetMapping(value = "/organization/{organizationId}/named", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SupportRequestDto>> getSupportRequestByOrganizationAndNamed(@Valid @PathVariable Long organizationId,
                                                                                               @RequestParam(defaultValue = "0") Integer page,
                                                                                               @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(supportService.getSupportRequestByOrganizationAndNamed(organizationId, PageRequest.of(page, size)), HttpStatus.OK);
    }

    @Operation(summary = "Get Support Request By Organization And User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Support Request are listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
            @ApiResponse(responseCode = "400", description = "Organization || User was not found",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
    })
    @GetMapping(value = "/organization/{organizationId}/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SupportRequestDto>> getSupportRequestByOrganizationAndUser(@Valid @PathVariable Long organizationId,
                                                                                          @Valid @PathVariable UUID userId,
                                                                                           @RequestParam(defaultValue = "0") Integer page,
                                                                                           @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(supportService.getSupportRequestByOrganizationAndUser(organizationId, userId, PageRequest.of(page, size)), HttpStatus.OK);
    }

    @Operation(summary = "Create Support Response")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Support Response is created"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
            @ApiResponse(responseCode = "400", description = "Support || User was not found",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
    })
    @PostMapping(value = "/response", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SupportThreadDto> createSupportResponse(@Valid @RequestBody CreatingSupportResponseDto creatingSupportResponse) {
        return new ResponseEntity<>(supportService.createSupportResponse(creatingSupportResponse), HttpStatus.CREATED);
    }

    @Operation(summary = "Update Support Request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Support Response is updated"),
            @ApiResponse(responseCode = "400", description = "Invalid Request or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
            @ApiResponse(responseCode = "400", description = "Support was not found",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
    })
    @PutMapping(value = "/{supportResponseId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateSupportRequest(@Valid @PathVariable Long supportResponseId,
                                                                  @Valid @RequestBody EditingSupportRequestDto editingSupportRequest) {
        supportService.updateSupportRequest(supportResponseId, editingSupportRequest);

        return ResponseEntity
                .status(HttpStatus.PERMANENT_REDIRECT)
                .header(HttpHeaders.LOCATION, ENDPOINT + "/" + supportResponseId)
                .build();
    }

    @Operation(summary = "Delete Support Request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Support Request is Deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
            @ApiResponse(responseCode = "400", description = "Support was not found",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
    })
    @DeleteMapping(value = "/{supportRequestId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSupportRequest(@Valid @PathVariable Long supportRequestId) {
        supportService.deleteSupportRequest(supportRequestId);
    }

    @Operation(summary = "Delete Support Thread")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Support Thread is Deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
            @ApiResponse(responseCode = "400", description = "Support Thread was not found",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
    })
    @DeleteMapping(value = "/thread/{supportThreadId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSupportRequestThread(@Valid @PathVariable Long supportThreadId) {
        supportService.deleteSupportThread(supportThreadId);
    }


}
