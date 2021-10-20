package com.schoolplus.office.web.controllers.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolplus.office.services.AccessTokenService;
import com.schoolplus.office.services.RefreshTokenService;
import com.schoolplus.office.web.exceptions.InvalidRequestException;
import com.schoolplus.office.web.models.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Token Controller", description = "Exposes token endpoints")
@Slf4j
@RequiredArgsConstructor
@RequestMapping(TokenController.ENDPOINT)
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
@RestController
public class TokenController {

    public static final String ENDPOINT = "/token";

    private final ObjectMapper objectMapper;
    private final RefreshTokenService refreshTokenService;
    private final AccessTokenService accessTokenService;

    @Operation(summary = "Get Access Token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access Token is generated"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "500",
                    description = "Server Error while generating Access Token", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            })
    })
    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<TokenResponseDto> getToken(@RequestParam Map<String, String> params) {

        TokenRequestDto tokenRequest = objectMapper.convertValue(params, TokenRequestDto.class);

        if (tokenRequest.getGrantType().equalsIgnoreCase(GrantType.REFRESH_TOKEN.getGrant())) {
            return new ResponseEntity<>(refreshTokenService.createToken(tokenRequest), HttpStatus.OK);
        } else if (tokenRequest.getGrantType().equalsIgnoreCase(GrantType.CHECK_TOKEN.getGrant())) {
            return new ResponseEntity<>(accessTokenService.checkToken(tokenRequest), HttpStatus.OK);
        } else if (tokenRequest.getGrantType().equalsIgnoreCase(GrantType.REVOKE.getGrant())) {
            return new ResponseEntity<>(refreshTokenService.revokeToken(tokenRequest), HttpStatus.OK);
        } else {
            log.warn("A client attempted a request with an invalid grant type [grantType: {}]", tokenRequest.getGrantType());
            throw new InvalidRequestException(ErrorDesc.INVALID_GRANT_TYPE.getDesc());
        }

    }

}
