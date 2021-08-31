package com.schoolplus.office.web.controllers.backoffice;

import com.schoolplus.office.services.AppointmentService;
import com.schoolplus.office.web.models.AppointmentDto;
import com.schoolplus.office.web.models.CreatingAppointmentDto;
import com.schoolplus.office.web.models.EditingAppointmentDto;
import com.schoolplus.office.web.models.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
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

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping(AppointmentManagementController.ENDPOINT)
@RestController
public class AppointmentManagementController {

    public final static String ENDPOINT = "/management/appointments";

    private final AppointmentService appointmentService;

    @Operation(summary = "Get Appointments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointments are listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AppointmentDto>> getAppointments(@RequestParam(defaultValue = "0") Integer page,
                                                                @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(appointmentService.getAppointments(PageRequest.of(page, size)), HttpStatus.OK);
    }

    @Operation(summary = "Get Appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment info is generated"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Appointment not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @GetMapping(value = "/{appointmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AppointmentDto> getAppointment(@Valid @PathVariable UUID appointmentId) {
        return new ResponseEntity<>(appointmentService.getAppointment(appointmentId), HttpStatus.OK);
    }

    @Operation(summary = "Create Appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Appointment is created"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Teacher || Student not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AppointmentDto> createAppointment(@Valid @RequestBody CreatingAppointmentDto creatingAppointment) {
        return new ResponseEntity<>(appointmentService.createAppointment(creatingAppointment), HttpStatus.CREATED);
    }

    @Operation(summary = "Update Appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "308", description = "Appointment is updated",
                    headers = @Header(name = "Location", description = "Location of the edited the Appointment.")),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Appointment || Teacher || Student not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @PutMapping(value = "/{appointmentId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateAppointment(@Valid @PathVariable UUID appointmentId,
                                            @Valid @RequestBody EditingAppointmentDto editingAppointment) {
        appointmentService.updateAppointment(appointmentId, editingAppointment);
        return ResponseEntity
                .status(HttpStatus.PERMANENT_REDIRECT)
                .header(HttpHeaders.LOCATION, ENDPOINT + "/" + appointmentId)
                .build();
    }

    @Operation(summary = "Delete Appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Appointment is deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Appointment not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @DeleteMapping(value = "/{appointmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAppointment(@Valid @PathVariable UUID appointmentId) {
        appointmentService.deleteAppointment(appointmentId);
    }

}
