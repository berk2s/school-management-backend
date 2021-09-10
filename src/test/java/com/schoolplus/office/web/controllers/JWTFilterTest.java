package com.schoolplus.office.web.controllers;

import com.schoolplus.office.security.SecurityUser;
import com.schoolplus.office.security.SecurityUserDetailsService;
import com.schoolplus.office.services.AccessTokenService;
import com.schoolplus.office.services.RefreshTokenService;
import com.schoolplus.office.web.models.AccessTokenCommand;
import com.schoolplus.office.web.models.RefreshTokenCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class JWTFilterTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccessTokenService accessTokenService;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    SecurityUserDetailsService securityUserDetailsService;

    String accessToken;
    String refreshToken;
    final String username = "username";

    void generateToken(boolean isExpired) {
        SecurityUser securityUser = securityUserDetailsService.loadUserByUsername(username);
        AccessTokenCommand accessTokenCommand = new AccessTokenCommand();
        accessTokenCommand.setSecurityUser(securityUser);
        accessTokenCommand.setScopes(List.of("profile:manage"));

        RefreshTokenCommand refreshTokenCommand = new RefreshTokenCommand();
        refreshTokenCommand.setSecurityUser(securityUser);

        refreshToken = refreshTokenService.createToken(refreshTokenCommand);
        if(isExpired) {
            accessTokenCommand.setExpiryDateTime(Duration.ZERO);
            accessToken = accessTokenService.createToken(accessTokenCommand);
        } else {
            accessToken = accessTokenService.createToken(accessTokenCommand);
        }
    }

    @DisplayName("Jwt Filter Works Successfully")
    @Test
    void jwtFilterWorksSuccessfully() throws Exception {

        generateToken(false);

        mockMvc.perform(get("/")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @DisplayName("Expired Jwt Error")
    @Test
    void expiredJwtFilterError() throws Exception {

        generateToken(true);

        mockMvc.perform(get("/")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isUnauthorized());

    }
}


