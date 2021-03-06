package com.schoolplus.office.services.impl;

import com.schoolplus.office.annotations.AuthenticationProcess;
import com.schoolplus.office.config.ServerConfiguration;
import com.schoolplus.office.security.SecurityUser;
import com.schoolplus.office.security.SecurityUserDetailsService;
import com.schoolplus.office.security.UserAuthenticationProvider;
import com.schoolplus.office.services.AccessTokenService;
import com.schoolplus.office.services.LoginService;
import com.schoolplus.office.services.RefreshTokenService;
import com.schoolplus.office.web.models.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class LoginServiceImpl implements LoginService {

    private final UserAuthenticationProvider userAuthenticationProvider;
    private final SecurityUserDetailsService securityUserDetailsService;
    private final ServerConfiguration serverConfiguration;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;

    @AuthenticationProcess(domain = TransactionDomain.LOGIN, action = DomainAction.LOGIN_SUCCESSFULLY)
    @Override
    public TokenResponseDto authenticate(LoginRequestDto loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        userAuthenticationProvider.authenticate(usernamePasswordAuthenticationToken);

        SecurityUser securityUser = securityUserDetailsService.loadUserByUsername(username);

        AccessTokenCommand accessTokenCommand = new AccessTokenCommand();
        accessTokenCommand.setSecurityUser(securityUser);
        accessTokenCommand.setScopes(Arrays.stream(loginRequest.getScopes().split(" ")).collect(Collectors.toList()));

        RefreshTokenCommand refreshTokenCommand = new RefreshTokenCommand();
        refreshTokenCommand.setSecurityUser(securityUser);

        String accessToken = accessTokenService.createToken(accessTokenCommand);
        String refreshToken = refreshTokenService.createToken(refreshTokenCommand);

        log.info("The User has been logged successfully [userId: {}]", securityUser.getId().toString());

        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(serverConfiguration.getAccessToken().getLifetime().toSeconds())
                .build();
    }
}