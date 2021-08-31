package com.schoolplus.office.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolplus.office.config.ServerConfiguration;
import com.schoolplus.office.web.models.ErrorDesc;
import com.schoolplus.office.web.models.ErrorType;
import com.schoolplus.office.web.models.LoginRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    LoginRequestDto loginRequest;

    @Autowired
    ServerConfiguration serverConfiguration;

    @BeforeEach
    void setUp() {
        loginRequest = LoginRequestDto.builder()
                .username("username")
                .password("password")
                .scopes(List.of("profile:manage"))
                .build();
    }

    @DisplayName("Login Successfully Returns 200")
    @Test
    void testLoginSuccessfully() throws Exception {

        mockMvc.perform(post(LoginController.ENDPOINT)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.access_token", matchesPattern("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$")))
                .andExpect(jsonPath("$.refresh_token", hasLength(48)))
                .andExpect(jsonPath("$.expires_in", is((int) serverConfiguration.getAccessToken().getLifetime().toSeconds())));

    }

    @DisplayName("Invalid username fails")
    @Test
    void invalidUsernameFails() throws Exception {

        loginRequest.setUsername("invalid_username");

        mockMvc.perform(post(LoginController.ENDPOINT)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is(ErrorType.INVALID_GRANT.getError())))
                .andExpect(jsonPath("$.error_description", is(ErrorDesc.BAD_CREDENTIALS.getDesc())));

    }

    @DisplayName("Invalid password fails")
    @Test
    void invalidPasswordFails() throws Exception {

        loginRequest.setPassword("invalid_password");

        mockMvc.perform(post(LoginController.ENDPOINT)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is(ErrorType.INVALID_GRANT.getError())))
                .andExpect(jsonPath("$.error_description", is(ErrorDesc.BAD_CREDENTIALS.getDesc())));

    }

    @DisplayName("Requested scopes are not valid for user fails")
    @Test
    void requestedScopesAreNotValidForUserFails() throws Exception {

        loginRequest.setScopes(List.of("invalid_scope", "invalid_scope_two"));

        mockMvc.perform(post(LoginController.ENDPOINT)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is(ErrorType.INVALID_GRANT.getError())))
                .andExpect(jsonPath("$.error_description", is(ErrorDesc.USER_HAS_NOT_SCOPE.getDesc())));

    }

    @DisplayName("Method argument not valid fails")
    @Test
    void methodArgumentNotValidFails() throws Exception {

        LoginRequestDto invalidLoginRequest = new LoginRequestDto();
        invalidLoginRequest.setUsername("username");

        mockMvc.perform(post(LoginController.ENDPOINT)
                        .content(objectMapper.writeValueAsString(invalidLoginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                .andExpect(jsonPath("$.error_description", is(ErrorDesc.BAD_REQUEST.getDesc())));

    }


}
