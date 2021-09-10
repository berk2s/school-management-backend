package com.schoolplus.office.web.controllers.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolplus.office.services.AccessTokenService;
import com.schoolplus.office.services.RefreshTokenService;
import com.schoolplus.office.web.exceptions.InvalidRequestException;
import com.schoolplus.office.web.models.ErrorDesc;
import com.schoolplus.office.web.models.GrantType;
import com.schoolplus.office.web.models.TokenRequestDto;
import com.schoolplus.office.web.models.TokenResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(TokenController.ENDPOINT)
@RestController
public class TokenController {

    public static final String ENDPOINT = "/token";

    private final ObjectMapper objectMapper;
    private final RefreshTokenService refreshTokenService;
    private final AccessTokenService accessTokenService;

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
