package com.schoolplus.office.web.controllers.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolplus.office.config.ServerConfiguration;
import com.schoolplus.office.domain.Authority;
import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.domain.RefreshToken;
import com.schoolplus.office.domain.User;
import com.schoolplus.office.repository.AuthorityRepository;
import com.schoolplus.office.repository.OrganizationRepository;
import com.schoolplus.office.repository.RefreshTokenRepository;
import com.schoolplus.office.repository.UserRepository;
import com.schoolplus.office.security.SecurityUser;
import com.schoolplus.office.services.AccessTokenService;
import com.schoolplus.office.web.models.AccessTokenCommand;
import com.schoolplus.office.web.models.ErrorDesc;
import com.schoolplus.office.web.models.ErrorType;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TokenControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    ServerConfiguration serverConfiguration;

    @Autowired
    AccessTokenService accessTokenService;

    User user;
    Authority authority;
    Organization organization;

    @BeforeEach
    void setUp() {
        organization = new Organization();

        organizationRepository.save(organization);

        authority = new Authority();
        authority.setAuthorityName(RandomStringUtils.random(10, true, false));

        authorityRepository.save(authority);

        user = new User();
        user.setOrganization(organization);
        user.addAuthority(authority);

        userRepository.save(user);
    }

    @DisplayName("Refreshing Token")
    @Nested
    class RefreshingToken {

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        RefreshToken refreshToken;
        String token;

        @BeforeEach
        void setUp() {
            token = RandomStringUtils.random(48, true, true);

            refreshToken = new RefreshToken();
            refreshToken.setToken(token);
            refreshToken.setUser(user);
            refreshToken.setNotBefore(LocalDateTime.now());
            refreshToken.setExpiryDateTime(LocalDateTime.now().plusMinutes(10));
            refreshToken.setIssueTime(LocalDateTime.now());

            refreshTokenRepository.save(refreshToken);

            params.set("client_id", "clientId");
            params.set("grant_type", "refresh_token");
            params.set("refresh_token", refreshToken.getToken());
            params.set("scopes", authority.getAuthorityName());
        }

        @DisplayName("Refresh a Token Successfully")
        @Test
        void refreshTokenSuccessfully() throws Exception {
            mockMvc.perform(post(TokenController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .params(params))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.access_token", matchesPattern("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$")))
                    .andExpect(jsonPath("$.refresh_token", is(refreshToken.getToken())))
                    .andExpect(jsonPath("$.expires_in", is((int) serverConfiguration.getAccessToken().getLifetime().toSeconds())));
        }

        @DisplayName("Refresh EMpty Token Error")
        @Test
        void refreshEmptyTokenError() throws Exception {
            params.remove("refresh_token");

            mockMvc.perform(post(TokenController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .params(params))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_GRANT.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.INVALID_TOKEN.getDesc())));
        }

        @DisplayName("Refresh Invalid Token Error")
        @Test
        void refreshInvalidTokenError() throws Exception {
            params.set("refresh_token", RandomStringUtils.random(48, true, true));

            mockMvc.perform(post(TokenController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .params(params))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_GRANT.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.INVALID_TOKEN.getDesc())));
        }

        @DisplayName("Refresh Expired Token Error")
        @Test
        void refreshExpiredTokenError() throws Exception {
            refreshToken.setExpiryDateTime(LocalDateTime.now().minusMinutes(60));

            refreshTokenRepository.save(refreshToken);

            mockMvc.perform(post(TokenController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .params(params))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_GRANT.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.INVALID_TOKEN.getDesc())));
        }

        @DisplayName("Refresh Token Invalid Scope Error")
        @Test
        void refreshTokenInvalidScopeError() throws Exception {
            params.set("scopes", RandomStringUtils.random(5, true, false));

            mockMvc.perform(post(TokenController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .params(params))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_GRANT.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.USER_HAS_NOT_SCOPE.getDesc())));
        }

    }

    @DisplayName("Checking Access Token")
    @Nested
    class CheckingAccessToken {

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        String token;

        @BeforeEach
        void setUp() {
            token = accessTokenService.createToken(AccessTokenCommand.builder()
                    .securityUser(new SecurityUser(user))
                    .scopes(List.of(authority.getAuthorityName()))
                    .build());

            params.set("client_id", "clientId");
            params.set("grant_type", "check_token");
            params.set("access_token", token);
        }

        @DisplayName("Check Access Token Successfully")
        @Test
        void checkAccessTokenSuccessfully() throws Exception {
            mockMvc.perform(post(TokenController.ENDPOINT)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .params(params))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @DisplayName("Check Access Token Empty Error")
        @Test
        void checkAccessTokenEmptyError() throws Exception {
            params.remove("access_token");

            mockMvc.perform(post(TokenController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .params(params))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_GRANT.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.INVALID_TOKEN.getDesc())));
        }

        @DisplayName("Check Access Token Invalid Token Error")
        @Test
        void checkAccessTokenInvalidTokenError() throws Exception {
            params.set("access_token", RandomStringUtils.random(100, true, false));

            mockMvc.perform(post(TokenController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .params(params))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_GRANT.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.INVALID_TOKEN.getDesc())));
        }

        @DisplayName("Check Access Token Expired Token Error")
        @Test
        void checkAccessTokenExpiredTokenError() throws Exception {
            String newToken = accessTokenService.createToken(AccessTokenCommand.builder()
                    .securityUser(new SecurityUser(user))
                    .expiryDateTime(Duration.ofSeconds(1))
                    .scopes(List.of(authority.getAuthorityName()))
                    .build());

            params.set("access_token", newToken);

            mockMvc.perform(post(TokenController.ENDPOINT)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .params(params))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_GRANT.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.INVALID_TOKEN.getDesc())));
        }

    }
}
