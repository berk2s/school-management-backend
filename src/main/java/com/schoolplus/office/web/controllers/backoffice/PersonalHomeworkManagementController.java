package com.schoolplus.office.web.controllers.backoffice;

import com.schoolplus.office.web.models.CreatingPersonalHomeworkDto;
import com.schoolplus.office.web.models.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping(PersonalHomeworkManagementController.ENDPOINT)
@RestController
public class PersonalHomeworkManagementController {

    public static final String ENDPOINT = "/management/personal/homeworks";

    @Operation(summary = "Create Personal Homework")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Personal Homework is created"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Student || Teacher || Lesson not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createPersonalHomework(@Valid @RequestBody CreatingPersonalHomeworkDto creatingPersonalHomework) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


}
