package com.schoolplus.office.web.controllers;

import com.schoolplus.office.security.SecurityUser;
import com.schoolplus.office.services.UserInformationService;
import com.schoolplus.office.web.models.ChangingPasswordDto;
import com.schoolplus.office.web.models.EditingUserInformationDto;
import com.schoolplus.office.web.models.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "User Controller", description = "Exposes user information endpoints")
@RequiredArgsConstructor
@RequestMapping(UserController.ENDPOINT)
@RestController
public class UserController {

    public static final String ENDPOINT = "/user";

    private final UserInformationService userInformationService;

    @Operation(summary = "Update User Information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Profile is updated"),

            @ApiResponse(responseCode = "404", description = "User was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUser(@Valid @RequestBody EditingUserInformationDto editingUserInformationDto,
                           Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        userInformationService.editProfile(securityUser.getId(), editingUserInformationDto);
    }

    @Operation(summary = "Update User Password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Password is updated"),

            @ApiResponse(responseCode = "404", description = "User was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @PutMapping(value = "/password", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePassword(@Valid @RequestBody ChangingPasswordDto changingPasswordDto,
                           Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        userInformationService.updatePassword(securityUser.getId(), changingPasswordDto);
    }


}
