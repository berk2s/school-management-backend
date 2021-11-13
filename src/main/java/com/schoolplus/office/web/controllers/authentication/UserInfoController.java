package com.schoolplus.office.web.controllers.authentication;

import com.schoolplus.office.security.SecurityUser;
import com.schoolplus.office.services.UserInfoService;
import com.schoolplus.office.web.models.ErrorResponseDto;
import com.schoolplus.office.web.models.UserDto;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
@Tag(name = "User Info Controller", description = "Exposes user info endpoints")
@RequiredArgsConstructor
@RequestMapping(UserInfoController.ENDPOINT)
@RestController
public class UserInfoController {

    public static final String ENDPOINT = "/userinfo";

    private final UserInfoService userInfoService;

    @Operation(summary = "Get User Info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User Info is generated"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> getUserInfo(Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return new ResponseEntity<>(userInfoService.getUserInfo(securityUser.getId()), HttpStatus.OK);
    }

}
