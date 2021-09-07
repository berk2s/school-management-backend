package com.schoolplus.office.web.controllers.backoffice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolplus.office.domain.Announcement;
import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.repository.AnnouncementRepository;
import com.schoolplus.office.repository.OrganizationRepository;
import com.schoolplus.office.web.models.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AnnouncementManagementControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AnnouncementRepository announcementRepository;

    @Autowired
    OrganizationRepository organizationRepository;

    Organization organization;

    @BeforeEach
    void setUp() {
        organization = new Organization();
        organization.setOrganizationName(RandomStringUtils.random(10, true, false));

        organizationRepository.save(organization);
    }

    @DisplayName("Getting Announcement")
    @Nested
    class GettingAnnouncement {

        Announcement announcement;
        Organization organization;

        @BeforeEach
        void setUp() {
            organization = new Organization();
            organization.setOrganizationName(RandomStringUtils.random(10,true,false));

            organizationRepository.save(organization);

            Announcement _announcement = new Announcement();
            _announcement.setAnnouncementTitle(RandomStringUtils.random(10, true, false));
            _announcement.setAnnouncementDescription(RandomStringUtils.random(10, true, false));
            _announcement.addChannel(AnnouncementChannel.STUDENTS);
            _announcement.addImage("imageUrl");
            _announcement.setOrganization(organization);

            announcement = announcementRepository.save(_announcement);

            for (int i = 0; i < 20; i++) {
                Announcement newAnnouncement = new Announcement();
                newAnnouncement.setAnnouncementTitle(RandomStringUtils.random(10, true, false));
                newAnnouncement.setAnnouncementDescription(RandomStringUtils.random(10, true, false));
                newAnnouncement.addChannel(AnnouncementChannel.STUDENTS);
                newAnnouncement.setOrganization(organization);
                newAnnouncement.addImage("imageUrl");

                announcementRepository.save(newAnnouncement);
            }
        }

        @DisplayName("Get Announcement Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:announcements"})
        @Test
        void getAnnouncementSuccessfully() throws Exception {

            mockMvc.perform(get(AnnouncementManagementController.ENDPOINT + "/" + announcement.getId()))
                    .andDo(print())
                    .andExpect(jsonPath("$.announcementId").isNotEmpty())
                    .andExpect(jsonPath("$.announcementTitle", is(announcement.getAnnouncementTitle())))
                    .andExpect(jsonPath("$.announcementDescription", is(announcement.getAnnouncementDescription())))
                    .andExpect(jsonPath("$..announcementChannels[*]", anyOf(hasItem(is(announcement.getAnnouncementChannels().get(0).name())))))
                    .andExpect(jsonPath("$..announcementImages[*]", anyOf(hasItem(is(announcement.getAnnouncementImages().get(0))))))
                    .andExpect(jsonPath("$.organization.organizationName", is(organization.getOrganizationName())))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());

        }

        @DisplayName("Get Announcement Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:announcements"})
        @Test
        void getAnnouncementNotFoundError() throws Exception {
            mockMvc.perform(get(AnnouncementManagementController.ENDPOINT + "/123123123"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ANNOUNCEMENT_NOT_FOUND.getDesc())));
        }

        @DisplayName("Get Announcements Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:announcements"})
        @Test
        void getAnnouncementsSuccessfully() throws Exception {

            mockMvc.perform(get(AnnouncementManagementController.ENDPOINT + "?page=0&size=5"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..length()", is(5)))
                    .andExpect(jsonPath("$..announcementId").isNotEmpty())
                    .andExpect(jsonPath("$..announcementTitle").isNotEmpty())
                    .andExpect(jsonPath("$..announcementDescription").isNotEmpty())
                    .andExpect(jsonPath("$..announcementChannels[*]").isNotEmpty())
                    .andExpect(jsonPath("$..announcementImages[*]").isNotEmpty())
                    .andExpect(jsonPath("$..organization.organizationName").isNotEmpty())
                    .andExpect(jsonPath("$..createdAt").isNotEmpty())
                    .andExpect(jsonPath("$..lastModifiedAt").isNotEmpty());

        }
    }

    @DisplayName("Creating Announcement")
    @Nested
    class CreatingAnnouncement {

        CreatingAnnouncementDto creatingAnnouncement;
        Organization organization;

        @BeforeEach
        void setUp() {
            organization = new Organization();
            organization.setOrganizationName(RandomStringUtils.random(10, true, false));

            organizationRepository.save(organization);

            StringBuilder announcementDescription = new StringBuilder();

            for (int i = 0; i < 50; i++) {
                announcementDescription.append(RandomStringUtils.random(10, true, false)).append(' ');
            }

            creatingAnnouncement = new CreatingAnnouncementDto();
            creatingAnnouncement.setAnnouncementTitle(RandomStringUtils.random(10, true, false));
            creatingAnnouncement.setAnnouncementDescription(announcementDescription.toString());
            creatingAnnouncement.setAnnouncementChannels(List.of(AnnouncementChannel.ALL.name()));
            creatingAnnouncement.setOrganizationId(organization.getId());
        }

        @DisplayName("Create Announcement Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:announcements"})
        @Test
        void createAnnouncementSuccessfully() throws Exception {
            mockMvc.perform(post(AnnouncementManagementController.ENDPOINT)
                            .content(objectMapper.writeValueAsString(creatingAnnouncement))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andExpect(jsonPath("$.announcementId").isNotEmpty())
                    .andExpect(jsonPath("$.announcementTitle", is(creatingAnnouncement.getAnnouncementTitle())))
                    .andExpect(jsonPath("$.announcementDescription", is(creatingAnnouncement.getAnnouncementDescription().toString())))
                    .andExpect(jsonPath("$..announcementChannels[*]", anyOf(hasItem(is(creatingAnnouncement.getAnnouncementChannels().get(0))))))
                    .andExpect(jsonPath("$.organization.organizationName", is(organization.getOrganizationName())))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());
        }

    }

    @DisplayName("Adding Image To Announcement")
    @Nested
    class AddingImageToAnnouncement {

        Announcement announcement;

        @BeforeEach
        void setUp() {
            Announcement _announcement = new Announcement();
            _announcement.setAnnouncementTitle(RandomStringUtils.random(10, true, false));
            _announcement.setAnnouncementDescription(RandomStringUtils.random(10, true, false));
            _announcement.addImage("imageUrl");
            _announcement.setOrganization(organization);

            announcement = announcementRepository.save(_announcement);
        }

        @DisplayName("Add Image To Announcement Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:announcements"})
        @Test
        void addImageToAnnouncementSuccessfully() throws Exception {

            InputStream fileInputStream = AnnouncementManagementControllerTest.class.getResourceAsStream("testimg.jpeg");
            MockMultipartFile file = new MockMultipartFile("images", "image.jpeg", MediaType.IMAGE_JPEG_VALUE, fileInputStream);

            mockMvc.perform(multipart(AnnouncementManagementController.ENDPOINT + "/" + announcement.getId() + "/upload/images")
                            .file(file))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.[0].announcementId").isNotEmpty())
                    .andExpect(jsonPath("$.[0].imageUrl").isNotEmpty())
                    .andExpect(jsonPath("$.[0].imageSize").isNotEmpty());
        }

        @DisplayName("Add Image To Announcement Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:announcements"})
        @Test
        void addImageToAnnouncementNotFoundError() throws Exception {

            InputStream fileInputStream = AnnouncementManagementControllerTest.class.getResourceAsStream("testimg.jpeg");
            MockMultipartFile file = new MockMultipartFile("images", "image.jpeg", MediaType.IMAGE_JPEG_VALUE, fileInputStream);

            mockMvc.perform(multipart(AnnouncementManagementController.ENDPOINT + "/123123123" + "/upload/images")
                            .file(file))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ANNOUNCEMENT_NOT_FOUND.getDesc())));

        }

    }

    @DisplayName("Deleting Announcement Image")
    @Nested
    class DeletingAnnouncementImage {

        Announcement announcement;

        @BeforeEach
        void setUp() {
            Announcement _announcement = new Announcement();
            _announcement.setAnnouncementTitle(RandomStringUtils.random(10, true, false));
            _announcement.setAnnouncementDescription(RandomStringUtils.random(10, true, false));
            _announcement.addImage("imageUrl");
            _announcement.setOrganization(organization);

            announcement = announcementRepository.save(_announcement);
        }

        @DisplayName("Delete Announcement Image Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:announcements"})
        @Test
        void deleteAnnouncementImageSuccessfully() throws Exception {

            DeletingAnnouncementImageDto deletingAnnouncementImage = new DeletingAnnouncementImageDto();
            deletingAnnouncementImage.setImageUrls(List.of("imageUrl"));

            mockMvc.perform(put(AnnouncementManagementController.ENDPOINT + "/" + announcement.getId() + "/delete/images")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(deletingAnnouncementImage)))
                    .andDo(print())
                    .andExpect(status().isNoContent());

        }

        @DisplayName("Delete Announcement Image Announcement Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:announcements"})
        @Test
        void deleteAnnouncementImageAnnouncementNotFoundError() throws Exception {

            DeletingAnnouncementImageDto deletingAnnouncementImage = new DeletingAnnouncementImageDto();
            deletingAnnouncementImage.setImageUrls(List.of("imageUrl"));

            mockMvc.perform(put(AnnouncementManagementController.ENDPOINT + "/1231231231" + "/delete/images")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(deletingAnnouncementImage)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ANNOUNCEMENT_NOT_FOUND.getDesc())));

        }

    }

    @DisplayName("Updating Announcement")
    @Nested
    class UpdatingAnnouncement {

        Announcement announcement;
        Organization newOrganization;

        @BeforeEach
        void setUp() {

            newOrganization = new Organization();
            newOrganization.setOrganizationName(RandomStringUtils.random(10, true, false));

            organizationRepository.saveAll(List.of(organization, newOrganization));

            Announcement _announcement = new Announcement();
            _announcement.setAnnouncementTitle(RandomStringUtils.random(10, true, false));
            _announcement.setAnnouncementDescription(RandomStringUtils.random(10, true, false));
            _announcement.addImage("imageUrl");
            _announcement.addChannel(AnnouncementChannel.STUDENTS);
            _announcement.setOrganization(organization);
            announcement = announcementRepository.save(_announcement);
        }

        @DisplayName("Update Announcement Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:announcements"})
        @Test
        void updateAnnouncementSuccessfully() throws Exception {

            EditingAnnouncementDto updatingAnnouncement = new EditingAnnouncementDto();
            updatingAnnouncement.setAnnouncementTitle(RandomStringUtils.random(10, true, false));
            updatingAnnouncement.setAnnouncementDescription(RandomStringUtils.random(10, true, false));
            updatingAnnouncement.setRemovedChannels(List.of(AnnouncementChannel.STUDENTS.name()));
            updatingAnnouncement.setAddedChannels(List.of(AnnouncementChannel.PARENTS.name()));
            updatingAnnouncement.setOrganizationId(newOrganization.getId());

            mockMvc.perform(put(AnnouncementManagementController.ENDPOINT + "/" + announcement.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatingAnnouncement)))
                    .andDo(print())
                    .andExpect(status().isPermanentRedirect())
                    .andExpect(redirectedUrl(AnnouncementManagementController.ENDPOINT + "/" + announcement.getId()));

            mockMvc.perform(get(AnnouncementManagementController.ENDPOINT + "/" + announcement.getId()))
                    .andDo(print())
                    .andExpect(jsonPath("$.announcementId").isNotEmpty())
                    .andExpect(jsonPath("$.announcementTitle", is(updatingAnnouncement.getAnnouncementTitle())))
                    .andExpect(jsonPath("$.announcementDescription", is(updatingAnnouncement.getAnnouncementDescription())))
                    .andExpect(jsonPath("$..announcementChannels[*]", anyOf(hasItem(is(updatingAnnouncement.getAddedChannels().get(0))))))
                    .andExpect(jsonPath("$.organization.organizationName", is(newOrganization.getOrganizationName())))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty());
        }

        @DisplayName("Update Announcement Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:announcements"})
        @Test
        void updateAnnouncementNotFoundError() throws Exception {
            EditingAnnouncementDto updatingAnnouncement = new EditingAnnouncementDto();
            updatingAnnouncement.setAnnouncementTitle(RandomStringUtils.random(10, true, false));
            updatingAnnouncement.setAnnouncementDescription(RandomStringUtils.random(10, true, false));

            mockMvc.perform(put(AnnouncementManagementController.ENDPOINT + "/123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatingAnnouncement)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ANNOUNCEMENT_NOT_FOUND.getDesc())));

        }

        @DisplayName("Update Announcement Organization Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:announcements"})
        @Test
        void updateAnnouncementOrganizationNotFoundError() throws Exception {
            EditingAnnouncementDto updatingAnnouncement = new EditingAnnouncementDto();
            updatingAnnouncement.setAnnouncementTitle(RandomStringUtils.random(10, true, false));
            updatingAnnouncement.setAnnouncementDescription(RandomStringUtils.random(10, true, false));
            updatingAnnouncement.setOrganizationId(12312321L);

            mockMvc.perform(put(AnnouncementManagementController.ENDPOINT + "/" + announcement.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatingAnnouncement)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc())));

        }

    }

    @DisplayName("Deleting Announcement")
    @Nested
    class DeletingAnnouncement {

        Announcement announcement;

        @BeforeEach
        void setUp() {
            Announcement _announcement = new Announcement();
            _announcement.setAnnouncementTitle(RandomStringUtils.random(10, true, false));
            _announcement.setAnnouncementDescription(RandomStringUtils.random(10, true, false));
            _announcement.setOrganization(organization);

            announcement = announcementRepository.save(_announcement);
        }

        @DisplayName("Delete Announcement Successfully")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:announcements"})
        @Test
        void deleteAnnouncementSuccessfully() throws Exception {
            mockMvc.perform(delete(AnnouncementManagementController.ENDPOINT + '/' + announcement.getId()))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @DisplayName("Delete Announcement Not Found Error")
        @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "manage:announcements"})
        @Test
        void deleteAnnouncementNotFoundError() throws Exception {
            mockMvc.perform(delete(AnnouncementManagementController.ENDPOINT + "/123123123"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is(ErrorType.INVALID_REQUEST.getError())))
                    .andExpect(jsonPath("$.error_description", is(ErrorDesc.ANNOUNCEMENT_NOT_FOUND.getDesc())));
        }

    }

}
