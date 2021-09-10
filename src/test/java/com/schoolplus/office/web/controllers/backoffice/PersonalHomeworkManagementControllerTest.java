package com.schoolplus.office.web.controllers.backoffice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolplus.office.web.models.CreatingPersonalHomeworkDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PersonalHomeworkManagementControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @DisplayName("Creating Personal Homework")
    @Nested
    class CreatingPersonalHomework {

        CreatingPersonalHomeworkDto creatingPersonalHomework;

        @DisplayName("Create Personal Homework Successfully")
        @WithMockUser(username = "username",  authorities = {"ROLE_ADMIN", "manage:users:students"})
        @Test
        void createPersonalHomeworkSuccessfully() throws Exception {
            mockMvc.perform(post(PersonalHomeworkManagementController.ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(creatingPersonalHomework)))
                    .andExpect(status().isCreated());
        }

    }
}
