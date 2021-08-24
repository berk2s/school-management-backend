package com.schoolplus.office.services.impl;

import com.schoolplus.office.config.ServerConfiguration;
import com.schoolplus.office.domain.RefreshToken;
import com.schoolplus.office.domain.User;
import com.schoolplus.office.repository.RefreshTokenRepository;
import com.schoolplus.office.repository.UserRepository;
import com.schoolplus.office.services.RefreshTokenService;
import com.schoolplus.office.web.exceptions.UserNotFoundException;
import com.schoolplus.office.web.models.ErrorDesc;
import com.schoolplus.office.web.models.RefreshTokenCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final ServerConfiguration serverConfiguration;

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

}
