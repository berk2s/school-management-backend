package com.schoolplus.office.services.impl;

import com.schoolplus.office.config.ServerConfiguration;
import com.schoolplus.office.security.SecurityUser;
import com.schoolplus.office.security.SecurityUserDetailsService;
import com.schoolplus.office.security.UserAuthenticationProvider;
import com.schoolplus.office.services.AccessTokenService;
import com.schoolplus.office.services.LoginService;
import com.schoolplus.office.services.RefreshTokenService;
import com.schoolplus.office.web.models.AccessTokenCommand;
import com.schoolplus.office.web.models.LoginRequestDto;
import com.schoolplus.office.web.models.LoginResponseDto;
import com.schoolplus.office.web.models.RefreshTokenCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class LoginServiceImpl implements LoginService {

    private final UserAuthenticationProvider userAuthenticationProvider;
    private final SecurityUserDetailsService securityUserDetailsService;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public LoginResponseDto authenticate(LoginRequestDto loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        userAuthenticationProvider.authenticate(usernamePasswordAuthenticationToken);

        SecurityUser securityUser = securityUserDetailsService.loadUserByUsername(username);

        AccessTokenCommand accessTokenCommand = new AccessTokenCommand();
        accessTokenCommand.setSecurityUser(securityUser);
        accessTokenCommand.setScopes(loginRequest.getScopes());

        RefreshTokenCommand refreshTokenCommand = new RefreshTokenCommand();
        refreshTokenCommand.setSecurityUser(securityUser);

        String accessToken = accessTokenService.createToken(accessTokenCommand);
        String refreshToken = refreshTokenService.createToken(refreshTokenCommand);

        log.info("The User has been logged successfully [userId: {}]", securityUser.getId().toString());

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}