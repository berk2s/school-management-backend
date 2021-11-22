package com.schoolplus.office.web.controllers;

import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.domain.User;
import com.schoolplus.office.repository.OrganizationRepository;
import com.schoolplus.office.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class ValidationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    OrganizationRepository organizationRepository;

    User user;
    Organization organization;



    @BeforeEach
    void setUp() {
        organization = new Organization();
        organization.setOrganizationName(RandomStringUtils.random(10, true, false));

        organizationRepository.save(organization);

        user = new User();
        user.setUsername(RandomStringUtils.random(10, true, false));
        user.setPhoneNumber(RandomStringUtils.random(10, true, false));
        user.setEmail(RandomStringUtils.random(10, true, false));
        user.setOrganization(organization);

        userRepository.save(user);
    }

    @DisplayName("Validate Username Successfully")
    @WithMockUser(username = "username", authorities = {"ROLE_USER"})
    @Test
    void validateUsernameSuccessfully() throws Exception {
        mockMvc.perform(get(ValidationController.ENDPOINT + "/username/" + user.getUsername()))
                .andDo(print())
                .andExpect(jsonPath("$.isTaken", is(true)));
    }

    @DisplayName("Validate Phone Number Successfully")
    @WithMockUser(username = "username", authorities = {"ROLE_USER"})
    @Test
    void validatePhoneNumberSuccessfully() throws Exception {
        mockMvc.perform(get(ValidationController.ENDPOINT + "/phonenumber/" + user.getPhoneNumber()))
                .andDo(print())
                .andExpect(jsonPath("$.isTaken", is(true)));
    }

    @DisplayName("Validate Email Successfully")
    @WithMockUser(username = "username", authorities = {"ROLE_USER"})
    @Test
    void validateEmailSuccessfully() throws Exception {
        mockMvc.perform(get(ValidationController.ENDPOINT + "/email/" + user.getEmail()))
                .andDo(print())
                .andExpect(jsonPath("$.isTaken", is(true)));
    }

}
