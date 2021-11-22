package com.schoolplus.office.web.controllers;

import com.schoolplus.office.services.MetadataService;
import com.schoolplus.office.web.models.ErrorResponseDto;
import com.schoolplus.office.web.models.MetadataDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Metadata Controller", description = "Exposes some public informations")
@RequiredArgsConstructor
@RequestMapping(MetadataController.ENDPOINT)
@RestController
public class MetadataController {

    public static final String ENDPOINT = "/metadata";

    private final MetadataService metadataService;

    @Operation(summary = "Get Metadatas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metadata info is generated"),
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MetadataDto> getMetadata() {
        return new ResponseEntity<>(metadataService.getMetadata(), HttpStatus.OK);
    }
}
