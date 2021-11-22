package com.schoolplus.office.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolplus.office.domain.Authority;
import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.domain.User;
import com.schoolplus.office.repository.AuthorityRepository;
import com.schoolplus.office.repository.OrganizationRepository;
import com.schoolplus.office.repository.UserRepository;
import com.schoolplus.office.security.SecurityUser;
import com.schoolplus.office.services.AccessTokenService;
import com.schoolplus.office.web.models.AccessTokenCommand;
import com.schoolplus.office.web.models.EditingUserInformationDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    AccessTokenService accessTokenService;

    User user;
    Organization organization;

    String accessToken;

    @BeforeEach
    void setUp() {
        organization = new Organization();
        organization.setOrganizationName(RandomStringUtils.random(10, true, false));

        organizationRepository.save(organization);

        Authority authority = new Authority();
        authority.setAuthorityName("profile:manage");

        authorityRepository.save(authority);

        user = new User();
        user.setUsername(RandomStringUtils.random(10, true, false));
        user.setFirstName(RandomStringUtils.random(10, true, false));
        user.setLastName(RandomStringUtils.random(10, true, false));
        user.setEmail(RandomStringUtils.random(10, true, false));
        user.setPhoneNumber(RandomStringUtils.random(10, true, false));
        user.setOrganization(organization);
        user.addAuthority(authority);

        userRepository.save(user);

        String token = accessTokenService.createToken(AccessTokenCommand.builder()
                .securityUser(new SecurityUser(user))
                .scopes(List.of("profile:manage"))
                .build());

        accessToken = "Bearer " + token;
    }

    @DisplayName("Edit User Successfully")
    @Test
    void editUserSuccessfully() throws Exception {

        EditingUserInformationDto editingUserInformationDto
                = new EditingUserInformationDto();
        editingUserInformationDto.setFirstName(RandomStringUtils.random(10, true, false));
        editingUserInformationDto.setLastName(RandomStringUtils.random(10, true, false));
        editingUserInformationDto.setUsername(RandomStringUtils.random(10, true, false));
        editingUserInformationDto.setPhoneNumber(RandomStringUtils.random(10, true, false));
        editingUserInformationDto.setEmail(RandomStringUtils.random(10, true, false));

        mockMvc.perform(put(UserController.ENDPOINT)
                        .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editingUserInformationDto)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

}
