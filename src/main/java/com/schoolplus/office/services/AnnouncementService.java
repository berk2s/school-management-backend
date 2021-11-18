package com.schoolplus.office.services;

import com.schoolplus.office.domain.Announcement;
import com.schoolplus.office.web.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AnnouncementService {

    List<AnnouncementDto> getAnnouncements(Pageable pageable);

    Page<AnnouncementDto> getAnnouncementsByOrganization(Long organizationId, Pageable pageable, String search);

    Page<AnnouncementDto> getAnnouncementsByOrganizationAndChannel(Long organizationId, Pageable pageable, AnnouncementChannel announcementChannel);

    AnnouncementDto getAnnouncement(Long announcementId);

    AnnouncementDto createAnnouncement(CreatingAnnouncementDto creatingAnnouncement);

    void updateAnnouncement(Long announcementId, EditingAnnouncementDto updatingAnnouncement);

    List<AnnouncementImageDto> uploadImages(Long announcementId, MultipartFile[] images);

    void deleteImages(Long announcementId, DeletingAnnouncementImageDto deletingAnnouncementImage);

    void deleteAnnouncement(Long announcementId);

}
