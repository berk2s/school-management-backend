package com.schoolplus.office.web.controllers;

import com.schoolplus.office.web.models.ErrorResponseDto;
import com.schoolplus.office.web.models.LoginRequestDto;
import com.schoolplus.office.web.models.LoginResponseDto;
import com.schoolplus.office.services.LoginService;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@Tag(name = "Login Controller", description = "Exposes login endpoint")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
@RequestMapping(LoginController.ENDPOINT)
@RestController
public class LoginController {

    public static final String ENDPOINT = "/login";

    private final LoginService loginService;

    @Operation(summary = "Post Login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The login request is successful"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "401",
                    description = "The login process is failed", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            })
    })
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponseDto> handleLogin(@Valid @RequestBody LoginRequestDto loginRequest) {
        return new ResponseEntity<>(loginService.authenticate(loginRequest), HttpStatus.OK);
    }

}
