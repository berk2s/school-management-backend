package com.schoolplus.office.services;

import com.schoolplus.office.web.models.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AnnouncementService {

    List<AnnouncementDto> getAnnouncements(Pageable pageable);

    AnnouncementDto getAnnouncement(Long announcementId);

    AnnouncementDto createAnnouncement(CreatingAnnouncementDto creatingAnnouncement);

    void updateAnnouncement(Long announcementId, UpdatingAnnouncementDto updatingAnnouncement);

    List<AnnouncementImageDto> uploadImages(Long announcementId, MultipartFile[] images);

    void deleteImages(Long announcementId, DeletingAnnouncementImageDto deletingAnnouncementImage);

    void deleteAnnouncement(Long announcementId);

}
