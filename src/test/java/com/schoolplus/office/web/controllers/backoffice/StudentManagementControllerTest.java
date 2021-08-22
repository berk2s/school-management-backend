package com.schoolplus.office.web.controllers.backoffice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolplus.office.domain.Authority;
import com.schoolplus.office.domain.Role;
import com.schoolplus.office.repository.AuthorityRepository;
import com.schoolplus.office.repository.RoleRepository;
import com.schoolplus.office.web.models.*;
import lombok.With;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Locale;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class StudentManagementControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    RoleRepository roleRepository;

    @DisplayName("Creating Student")
    @Nested
    class CreatingStudent {
        @DisplayName("Creating Student Successfully")
        @WithMockUser
        @Test
        void creatingStudentSuccessfully() throws Exception {

            Role role = roleRepository.findByRoleName("STUDENT").get();
            Authority authority = authorityRepository.findByAuthorityName("profile:manage").get();

            CreatingStudentDto creatingStudent = new CreatingStudentDto();
            creatingStudent.setUsername(RandomStringUtils.random(10, true, false));
            creatingStudent.setPassword(RandomStringUtils.random(10, true, false));
            creatingStudent.setFirstName(RandomStringUtils.random(10, true, false));
            creatingStudent.setLastName(RandomStringUtils.random(10, true, false));
            creatingStudent.setPhoneNumber(RandomStringUtils.random(10, true, false));
            creatingStudent.setEmail(RandomStringUtils.random(10, true, false));
            creatingStudent.setRoles(List.of(role.getId()));
            creatingStudent.setAuthorities(List.of(authority.getId()));
            creatingStudent.setIsAccountNonLocked(true);
            creatingStudent.setIsAccountNonExpired(true);
            creatingStudent.setIsCredentialsNonExpired(true);
            creatingStudent.setIsEnabled(true);
            creatingStudent.setGradeType(GradeType.HIGH_SCHOOL);
            creatingStudent.setGradeLevel(GradeLevel.ELEVENTH_GRADE);

            mockMvc.perform(post(StudentManagementController.ENDPOINT)
                            .content(objectMapper.writeValueAsString(creatingStudent))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.userId").isNotEmpty())
                    .andExpect(jsonPath("$.username", is(creatingStudent.getUsername())))
                    .andExpect(jsonPath("$.firstName", is(creatingStudent.getFirstName())))
                    .andExpect(jsonPath("$.lastName", is(creatingStudent.getLastName())))
                    .andExpect(jsonPath("$.phoneNumber", is(creatingStudent.getPhoneNumber())))
                    .andExpect(jsonPath("$.email", is(creatingStudent.getEmail())))
                    .andExpect(jsonPath("$.authorities[*]", anyOf(hasItem(is(authority.getAuthorityName())))))
                    .andExpect(jsonPath("$.roles", anyOf(hasItem(is(role.getRoleName().toUpperCase(Locale.ROOT))))))
                    .andExpect(jsonPath("$.isEnabled", is(creatingStudent.getIsEnabled())))
                    .andExpect(jsonPath("$.isAccountNonExpired", is(creatingStudent.getIsAccountNonExpired())))
                    .andExpect(jsonPath("$.isAccountNonLocked", is(creatingStudent.getIsAccountNonLocked())))
                    .andExpect(jsonPath("$.isCredentialsNonExpired", is(creatingStudent.getIsAccountNonExpired())))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty())
                    .andExpect(jsonPath("$.gradeType", is(creatingStudent.getGradeType().getType())))
                    .andExpect(jsonPath("$.gradeLevel", is(creatingStudent.getGradeLevel().getGradeYear())));
        }

        @DisplayName("Creating Student Role Not Found Error")
        @WithMockUser
        @Test
        void creatingStudentRoleNotFoundError() throws Exception {

//        Role role = roleRepository.findByRoleName("STUDENT").get();
            Authority authority = authorityRepository.findByAuthorityName("profile:manage").get();

            CreatingStudentDto creatingStudent = new CreatingStudentDto();
            creatingStudent.setUsername(RandomStringUtils.random(10, true, false));
            creatingStudent.setPassword(RandomStringUtils.random(10, true, false));
            creatingStudent.setFirstName(RandomStringUtils.random(10, true, false));
            creatingStudent.setLastName(RandomStringUtils.random(10, true, false));
            creatingStudent.setPhoneNumber(RandomStringUtils.random(10, true, false));
            creatingStudent.setEmail(RandomStringUtils.random(10, true, false));
            creatingStudent.setRoles(List.of(1231232L)); // invalid
            creatingStudent.setAuthorities(List.of(authority.getId()));
            creatingStudent.setIsAccountNonLocked(true);
            creatingStudent.setIsAccountNonExpired(true);
            creatingStudent.setIsCredentialsNonExpired(true);
            creatingStudent.setIsEnabled(true);
            creatingStudent.setGradeType(GradeType.HIGH_SCHOOL);
            creatingStudent.setGradeLevel(GradeLevel.ELEVENTH_GRADE);

            mockMvc.perform(post(StudentManagementController.ENDPOINT)
                            .content(objectMapper.writeValueAsString(creatingStudent))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ROLE_NOT_FOUND.getDesc())));
        }

        @DisplayName("Creating Student Authority Not Found Error")
        @WithMockUser
        @Test
        void creatingStudentAuthorityNotFoundError() throws Exception {

            Role role = roleRepository.findByRoleName("STUDENT").get();
//        Authority authority = authorityRepository.findByAuthorityName("profile:manage").get();

            CreatingStudentDto creatingStudent = new CreatingStudentDto();
            creatingStudent.setUsername(RandomStringUtils.random(10, true, false));
            creatingStudent.setPassword(RandomStringUtils.random(10, true, false));
            creatingStudent.setFirstName(RandomStringUtils.random(10, true, false));
            creatingStudent.setLastName(RandomStringUtils.random(10, true, false));
            creatingStudent.setPhoneNumber(RandomStringUtils.random(10, true, false));
            creatingStudent.setEmail(RandomStringUtils.random(10, true, false));
            creatingStudent.setRoles(List.of(role.getId()));
            creatingStudent.setAuthorities(List.of(31513L)); // invalid
            creatingStudent.setIsAccountNonLocked(true);
            creatingStudent.setIsAccountNonExpired(true);
            creatingStudent.setIsCredentialsNonExpired(true);
            creatingStudent.setIsEnabled(true);
            creatingStudent.setGradeType(GradeType.HIGH_SCHOOL);
            creatingStudent.setGradeLevel(GradeLevel.ELEVENTH_GRADE);

            mockMvc.perform(post(StudentManagementController.ENDPOINT)
                            .content(objectMapper.writeValueAsString(creatingStudent))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.AUTHORITY_NOT_FOUND.getDesc())));
        }
    }

}
