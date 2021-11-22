package com.schoolplus.office.web.controllers;

import com.schoolplus.office.services.ValidationService;
import com.schoolplus.office.web.models.ValidationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Validation Controller", description = "Exposes username, phone number and email validators")
@RequiredArgsConstructor
@RequestMapping(ValidationController.ENDPOINT)
@RestController
public class ValidationController {

    public static final String ENDPOINT = "/validate";

    private final ValidationService validationService;

    @Operation(summary = "Validate Username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Validation is successful"),
    })
    @GetMapping(value = "/username/{givenData}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ValidationDto> validateUsername(@PathVariable String givenData) {
        return new ResponseEntity<>(validationService.validateUsername(givenData), HttpStatus.OK);
    }

    @Operation(summary = "Validate Phone Number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Validation is successful"),
    })
    @GetMapping(value = "/phonenumber/{givenData}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ValidationDto> validatePhoneNumber(@PathVariable String givenData) {
        return new ResponseEntity<>(validationService.validatePhoneNumber(givenData), HttpStatus.OK);
    }

    @Operation(summary = "Validate E-Mail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Validation is successful"),
    })
    @GetMapping(value = "/email/{givenData}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ValidationDto> validateEmail(@PathVariable String givenData) {
        return new ResponseEntity<>(validationService.validateEmail(givenData), HttpStatus.OK);
    }

}
