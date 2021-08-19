package com.schoolplus.office.services.impl;

import com.schoolplus.office.config.ServerConfiguration;
import com.schoolplus.office.security.SecurityUser;
import com.schoolplus.office.services.AccessTokenService;
import com.schoolplus.office.services.JwtService;
import com.schoolplus.office.web.exceptions.InvalidGrantException;
import com.schoolplus.office.web.models.AccessTokenCommand;
import com.schoolplus.office.web.models.ErrorDesc;
import com.schoolplus.office.web.models.TokenCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
        Duration accessTokenDuration = serverConfiguration.getAccessToken().getLifetime();

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

        TokenCommand tokenCommand = TokenCommand.builder()
                .securityUser(securityUser)
                .expiryDateTime(accessTokenDuration)
                .claims(claims)
                .build();

        log.info("Access token has been created for the User in JWT format [userId: {}]", securityUser.getId().toString());

        return jwtService.createJwt(tokenCommand).serialize();
    }
}
