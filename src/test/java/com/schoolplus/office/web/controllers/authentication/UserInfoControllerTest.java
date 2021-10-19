package com.schoolplus.office.web.controllers.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolplus.office.domain.Authority;
import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.domain.Role;
import com.schoolplus.office.domain.User;
import com.schoolplus.office.repository.AuthorityRepository;
import com.schoolplus.office.repository.OrganizationRepository;
import com.schoolplus.office.repository.RoleRepository;
import com.schoolplus.office.repository.UserRepository;
import com.schoolplus.office.security.SecurityUser;
import com.schoolplus.office.services.AccessTokenService;
import com.schoolplus.office.web.models.AccessTokenCommand;
import com.schoolplus.office.web.models.ErrorDesc;
import com.schoolplus.office.web.models.ErrorType;
import com.schoolplus.office.web.models.UserType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserInfoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AccessTokenService accessTokenService;

    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    User user;
    Authority authority;
    Role role;
    Organization organization;

    String accessToken;

    @BeforeEach
    void setUp() {
        organization = new Organization();

        organizationRepository.save(organization);

        authority = new Authority();
        authority.setAuthorityName("profile:manage");

        authorityRepository.save(authority);

        role = new Role();
        role.setRoleName("USER");

        roleRepository.save(role);

        user = new User();
        user.setUsername(RandomStringUtils.random(10, true, false));
        user.setFirstName(RandomStringUtils.random(10, true, false));
        user.setLastName(RandomStringUtils.random(10, true, false));
        user.setEmail(RandomStringUtils.random(10, true, false));
        user.setPhoneNumber(RandomStringUtils.random(10, true, false));
        user.setOrganization(organization);
        user.addAuthority(authority);
        user.addRole(role);

        userRepository.save(user);

        String token = accessTokenService.createToken(AccessTokenCommand.builder()
                .securityUser(new SecurityUser(user))
                .scopes(List.of(authority.getAuthorityName()))
                .build());

        accessToken = "Bearer " + token;
    }

    @DisplayName("Getting User Info")
    @Nested
    class GettingUserInfo {

        @DisplayName("Get User Info Successfully")
        @Test
        void getUserInfoSuccessfully() throws Exception {
            mockMvc.perform(get(UserInfoController.ENDPOINT)
                    .header("Authorization", accessToken))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId", is(user.getId().toString())))
                    .andExpect(jsonPath("$.username", is(user.getUsername())))
                    .andExpect(jsonPath("$.firstName", is(user.getFirstName())))
                    .andExpect(jsonPath("$.lastName", is(user.getLastName())))
                    .andExpect(jsonPath("$.email", is(user.getEmail())))
                    .andExpect(jsonPath("$.phoneNumber", is(user.getPhoneNumber())))
                    .andExpect(jsonPath("$.authorities").isNotEmpty())
                    .andExpect(jsonPath("$.roles").isNotEmpty())
                    .andExpect(jsonPath("$.userType", is(UserType.USER.name())))
                    .andExpect(jsonPath("$.organization.organizationId", is(organization.getId().intValue())));
        }


    }

}
