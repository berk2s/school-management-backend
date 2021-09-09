package com.schoolplus.office.services.impl;

import com.nimbusds.jwt.JWTClaimsSet;
import com.schoolplus.office.config.ServerConfiguration;
import com.schoolplus.office.security.SecurityUser;
import com.schoolplus.office.services.AccessTokenService;
import com.schoolplus.office.services.JwtService;
import com.schoolplus.office.web.exceptions.InvalidGrantException;
import com.schoolplus.office.web.exceptions.TokenNotFoundException;
import com.schoolplus.office.web.models.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccessTokenServiceImpl implements AccessTokenService {

    private final ServerConfiguration serverConfiguration;
    private final JwtService jwtService;

    @Override
    public String createToken(AccessTokenCommand accessTokenCommand) {
        SecurityUser securityUser = accessTokenCommand.getSecurityUser();
        Duration configuredAccessTokenDuration = serverConfiguration.getAccessToken().getLifetime();

        Map<String, Object> claims = new HashMap<>();

        List<String> authorities = securityUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).filter(authority -> !authority.startsWith("ROLE_")).map(authority -> authority.toUpperCase(Locale.ROOT)).collect(Collectors.toList());
        List<String> roles = securityUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).filter(authority -> authority.startsWith("ROLE_")).map(authority -> authority.toUpperCase(Locale.ROOT)).collect(Collectors.toList());

        for (String _claimedAuthority : accessTokenCommand.getScopes()) {
            String claimedAuthority = _claimedAuthority.toUpperCase(Locale.ROOT);

            if(!authorities.contains(claimedAuthority)) {
                log.warn("The requested authority is not available for the User [userId: {}, authority: {}]", securityUser.getId().toString(), claimedAuthority);
                throw new InvalidGrantException(ErrorDesc.USER_HAS_NOT_SCOPE.getDesc());
            }

            claims.put("scopes", claimedAuthority);
        }

        claims.put("roles", roles);

        Duration accessTokenDuration = accessTokenCommand.getExpiryDateTime() != null ?
                accessTokenCommand.getExpiryDateTime() : configuredAccessTokenDuration;

        TokenCommand tokenCommand = TokenCommand.builder()
                .securityUser(securityUser)
                .expiryDateTime(accessTokenDuration)
                .claims(claims)
                .build();

        log.info("Access token has been created for the User in JWT format [userId: {}]", securityUser.getId().toString());

        return jwtService.createJwt(tokenCommand).serialize();
    }

    @Override
    public TokenResponseDto checkToken(TokenRequestDto tokenRequest) {
        if(tokenRequest.getAccessToken() == null) {
            log.warn("Token is empty");
            throw new InvalidGrantException(ErrorDesc.INVALID_TOKEN.getDesc());
        }

        String token = tokenRequest.getAccessToken();

        if (!jwtService.validate(token)) {
            log.warn("The given token couldn't validated [token: {}]", token);
            throw new InvalidGrantException(ErrorDesc.INVALID_TOKEN.getDesc());
        }

        JWTClaimsSet jwtClaimsSet = jwtService.parseAndValidate(token);

        LocalDateTime expiryDate = jwtClaimsSet.getExpirationTime()
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        if (expiryDate.isBefore(LocalDateTime.now())) {
            log.warn("The given token is expired [token: {}]", token);
            throw new InvalidGrantException(ErrorDesc.INVALID_TOKEN.getDesc());
        }

        return TokenResponseDto.builder().build();
    }
}
