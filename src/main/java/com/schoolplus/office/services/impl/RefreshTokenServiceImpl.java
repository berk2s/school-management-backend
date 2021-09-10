package com.schoolplus.office.services.impl;

import com.schoolplus.office.config.ServerConfiguration;
import com.schoolplus.office.domain.RefreshToken;
import com.schoolplus.office.domain.User;
import com.schoolplus.office.repository.RefreshTokenRepository;
import com.schoolplus.office.repository.UserRepository;
import com.schoolplus.office.security.SecurityUser;
import com.schoolplus.office.services.AccessTokenService;
import com.schoolplus.office.services.RefreshTokenService;
import com.schoolplus.office.utils.TokenUtils;
import com.schoolplus.office.web.exceptions.InvalidGrantException;
import com.schoolplus.office.web.exceptions.InvalidRequestException;
import com.schoolplus.office.web.exceptions.TokenNotFoundException;
import com.schoolplus.office.web.exceptions.UserNotFoundException;
import com.schoolplus.office.web.models.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final ServerConfiguration serverConfiguration;
    private final AccessTokenService accessTokenService;

    @Override
    public String createToken(RefreshTokenCommand refreshTokenCommand) {
        String generatedToken = RandomStringUtils.random(48, true, true);

        Duration refreshTokenDuration = serverConfiguration.getRefreshToken().getLifetime();

        LocalDateTime issuedAt = LocalDateTime.now();

        LocalDateTime expiryDateTime = LocalDateTime.now().plusMinutes(
                refreshTokenCommand.getExpiryDateTime() != null ?
                        refreshTokenCommand.getExpiryDateTime().toMinutes() :
                        refreshTokenDuration.toMinutes());

        LocalDateTime notBeforeTime = refreshTokenCommand.getNotBefore() != null ?
                LocalDateTime.now().plusMinutes(refreshTokenCommand.getNotBefore().toMinutes()) :
                LocalDateTime.now();

        User user = userRepository.findByUsername(refreshTokenCommand.getSecurityUser().getUsername())
                .orElseThrow(() -> {
                    log.warn("User with given username does not exists [username: {}]", refreshTokenCommand.getSecurityUser().getUsername());
                    throw new UserNotFoundException(ErrorDesc.USER_NOT_FOUND.getDesc());
                });

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(generatedToken);
        refreshToken.setIssueTime(issuedAt);
        refreshToken.setExpiryDateTime(expiryDateTime);
        refreshToken.setNotBefore(notBeforeTime);
        refreshToken.setUser(user);

        refreshTokenRepository.save(refreshToken);

        return refreshToken.getToken();
    }

    @Override
    public TokenResponseDto createToken(TokenRequestDto tokenRequest) {
        if (StringUtils.isEmpty(tokenRequest.getRefreshToken())) {
            log.warn("Refresh token is empty or it was not inside request [refreshToken: {}]", tokenRequest.getRefreshToken());
            throw new InvalidGrantException(ErrorDesc.INVALID_TOKEN.getDesc());
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenRequest.getRefreshToken())
                .orElseThrow(() -> {
                    log.warn("Refresh token with given id does not exists [refreshToken: {}]", tokenRequest.getRefreshToken());
                    throw new TokenNotFoundException(ErrorDesc.INVALID_TOKEN.getDesc());
                });

        if (!TokenUtils.isValid(refreshToken.getExpiryDateTime(), refreshToken.getNotBefore())) {
            log.warn("Refresh token is expired or not active [refreshToken: {}]", tokenRequest.getRefreshToken());
            throw new TokenNotFoundException(ErrorDesc.INVALID_TOKEN.getDesc());
        }

        List<String> scopes = (tokenRequest.getScopes() != null && tokenRequest.getScopes().length() != 0)
                ? Arrays.stream(tokenRequest.getScopes().split(" ")).map(scope -> scope.toUpperCase(Locale.ROOT)).collect(Collectors.toList())
                : List.of("profile:manage");

        AccessTokenCommand accessTokenCommand = new AccessTokenCommand();
        accessTokenCommand.setSecurityUser(new SecurityUser(refreshToken.getUser()));
        accessTokenCommand.setScopes(scopes);

        String accessToken = accessTokenService.createToken(accessTokenCommand);

        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(serverConfiguration.getAccessToken().getLifetime().getSeconds())
                .build();
    }

    @Transactional
    @Override
    public TokenResponseDto revokeToken(TokenRequestDto tokenRequest) {
        if (StringUtils.isEmpty(tokenRequest.getRefreshToken())) {
            log.warn("Refresh token is empty or it was not inside request [refreshToken: {}]", tokenRequest.getRefreshToken());
            throw new InvalidGrantException(ErrorDesc.INVALID_TOKEN.getDesc());
        }

        if (!refreshTokenRepository.existsByToken(tokenRequest.getRefreshToken())) {
            log.warn("Refresh token is empty or it was not inside request [refreshToken: {}]", tokenRequest.getRefreshToken());
            throw new InvalidGrantException(ErrorDesc.INVALID_TOKEN.getDesc());
        }

        refreshTokenRepository.deleteByToken(tokenRequest.getRefreshToken());

        return TokenResponseDto.builder().build();
    }

}
