package com.schoolplus.office.web.controllers.backoffice;

import com.schoolplus.office.services.AppointmentService;
import com.schoolplus.office.web.models.AppointmentDto;
import com.schoolplus.office.web.models.CreatingAppointmentDto;
import com.schoolplus.office.web.models.EditingAppointmentDto;
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

    @GetMapping(value = "/{appointmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AppointmentDto> getAppointment(@Valid @PathVariable UUID appointmentId) {
        return new ResponseEntity<>(appointmentService.getAppointment(appointmentId), HttpStatus.OK);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AppointmentDto>> getAppointments(@RequestParam(defaultValue = "0") Integer page,
                                                                @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(appointmentService.getAppointments(PageRequest.of(page, size)), HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AppointmentDto> createAppointment(@Valid @RequestBody CreatingAppointmentDto creatingAppointment) {
        return new ResponseEntity<>(appointmentService.createAppointment(creatingAppointment), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{appointmentId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateAppointment(@Valid @PathVariable UUID appointmentId,
                                            @Valid @RequestBody EditingAppointmentDto editingAppointment) {
        appointmentService.updateAppointment(appointmentId, editingAppointment);
        return ResponseEntity
                .status(HttpStatus.PERMANENT_REDIRECT)
                .header(HttpHeaders.LOCATION, ENDPOINT + "/" + appointmentId)
                .build();
    }

    @DeleteMapping(value = "/{appointmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAppointment(@Valid @PathVariable UUID appointmentId) {
        appointmentService.deleteAppointment(appointmentId);
    }

}
