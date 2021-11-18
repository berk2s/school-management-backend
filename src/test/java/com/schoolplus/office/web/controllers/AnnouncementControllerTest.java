package com.schoolplus.office.web.controllers;

import com.schoolplus.office.domain.Announcement;
import com.schoolplus.office.domain.AnnouncementImage;
import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.repository.AnnouncementRepository;
import com.schoolplus.office.repository.OrganizationRepository;
import com.schoolplus.office.web.models.AnnouncementChannel;
import com.schoolplus.office.web.models.ErrorDesc;
import com.schoolplus.office.web.models.ErrorType;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class AnnouncementControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    AnnouncementRepository announcementRepository;

    Organization organization;

    @BeforeEach
    void setUp() {
        organization = new Organization();
        organization.setOrganizationName(RandomStringUtils.random(10, true, false));

        organizationRepository.save(organization);
    }

    @DisplayName("Student Announcements")
    @Nested
    class StudentAnnouncements {

        @BeforeEach
        void setUp() {
            for (int i = 0; i < 5; i++) {
                AnnouncementImage announcementImage = new AnnouncementImage();
                announcementImage.setPath(RandomStringUtils.random(10, true, false));
                announcementImage.setFileName(RandomStringUtils.random(10, true, false));

                Announcement newAnnouncement = new Announcement();
                newAnnouncement.setAnnouncementTitle(RandomStringUtils.random(10, true, false));
                newAnnouncement.setAnnouncementDescription(RandomStringUtils.random(10, true, false));
                newAnnouncement.addChannel(AnnouncementChannel.STUDENTS);
                newAnnouncement.setOrganization(organization);
                newAnnouncement.addImage(announcementImage);

                announcementRepository.save(newAnnouncement);
            }
        }

        @DisplayName("Get Student Announcements By Organization Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_USER", "view:announcements"})
        @Test
        void getStudentAnnouncementsByOrganizationSuccessfully() throws Exception {

            mockMvc.perform(get(AnnouncementController.ENDPOINT + "/organization/" + organization.getId() + "?announcementChannel=STUDENTS"))
                    .andDo(print())
                    .andExpect(jsonPath("$.content..announcementId").isNotEmpty())
                    .andExpect(jsonPath("$.content..announcementTitle").isNotEmpty())
                    .andExpect(jsonPath("$.content..announcementDescription").isNotEmpty())
                    .andExpect(jsonPath("$.content..announcementChannels[*]").isNotEmpty())
                    .andExpect(jsonPath("$.content..announcementImages[*]").isNotEmpty())
                    .andExpect(jsonPath("$.content..createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.content..lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Get Student Announcements By Organization Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_USER", "view:announcements"})
        @Test
        void getStudentAnnouncementsByOrganizationNotFoundError() throws Exception {

            mockMvc.perform(get(AnnouncementController.ENDPOINT + "/organization/" + RandomUtils.nextLong() + "?announcementChannel=STUDENTS"))
                    .andDo(print())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));

        }

    }

    @DisplayName("Parent Announcements")
    @Nested
    class ParentAnnouncements {

        @BeforeEach
        void setUp() {
            for (int i = 0; i < 5; i++) {
                AnnouncementImage announcementImage = new AnnouncementImage();
                announcementImage.setPath(RandomStringUtils.random(10, true, false));
                announcementImage.setFileName(RandomStringUtils.random(10, true, false));

                Announcement newAnnouncement = new Announcement();
                newAnnouncement.setAnnouncementTitle(RandomStringUtils.random(10, true, false));
                newAnnouncement.setAnnouncementDescription(RandomStringUtils.random(10, true, false));
                newAnnouncement.addChannel(AnnouncementChannel.PARENTS);
                newAnnouncement.setOrganization(organization);
                newAnnouncement.addImage(announcementImage);

                announcementRepository.save(newAnnouncement);
            }
        }

        @DisplayName("Get Parent Announcements By Organization Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_USER", "view:announcements"})
        @Test
        void getTeacherAnnouncementsByOrganizationSuccessfully() throws Exception {

            mockMvc.perform(get(AnnouncementController.ENDPOINT + "/organization/" + organization.getId() + "?announcementChannel=PARENTS"))
                    .andDo(print())
                    .andExpect(jsonPath("$.content..announcementId").isNotEmpty())
                    .andExpect(jsonPath("$.content..announcementTitle").isNotEmpty())
                    .andExpect(jsonPath("$.content..announcementDescription").isNotEmpty())
                    .andExpect(jsonPath("$.content..announcementChannels[*]").isNotEmpty())
                    .andExpect(jsonPath("$.content..announcementImages[*]").isNotEmpty())
                    .andExpect(jsonPath("$.content..createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.content..lastModifiedAt").isNotEmpty());

        }

    }

    @DisplayName("Teacher Announcements")
    @Nested
    class TeacherAnnouncements {

        @BeforeEach
        void setUp() {
            for (int i = 0; i < 5; i++) {
                AnnouncementImage announcementImage = new AnnouncementImage();
                announcementImage.setPath(RandomStringUtils.random(10, true, false));
                announcementImage.setFileName(RandomStringUtils.random(10, true, false));

                Announcement newAnnouncement = new Announcement();
                newAnnouncement.setAnnouncementTitle(RandomStringUtils.random(10, true, false));
                newAnnouncement.setAnnouncementDescription(RandomStringUtils.random(10, true, false));
                newAnnouncement.addChannel(AnnouncementChannel.TEACHERS);
                newAnnouncement.setOrganization(organization);
                newAnnouncement.addImage(announcementImage);

                announcementRepository.save(newAnnouncement);
            }
        }

        @DisplayName("Get Teacher Announcements By Organization Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_USER", "view:announcements"})
        @Test
        void getTeacherAnnouncementsByOrganizationSuccessfully() throws Exception {

            mockMvc.perform(get(AnnouncementController.ENDPOINT + "/organization/" + organization.getId() + "?announcementChannel=TEACHERS"))
                    .andDo(print())
                    .andExpect(jsonPath("$.content..announcementId").isNotEmpty())
                    .andExpect(jsonPath("$.content..announcementTitle").isNotEmpty())
                    .andExpect(jsonPath("$.content..announcementDescription").isNotEmpty())
                    .andExpect(jsonPath("$.content..announcementChannels[*]").isNotEmpty())
                    .andExpect(jsonPath("$.content..announcementImages[*]").isNotEmpty())
                    .andExpect(jsonPath("$.content..createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.content..lastModifiedAt").isNotEmpty());

        }

    }

}
