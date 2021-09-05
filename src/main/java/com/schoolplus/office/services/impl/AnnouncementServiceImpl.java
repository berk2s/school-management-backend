package com.schoolplus.office.services.impl;

import com.schoolplus.office.domain.Announcement;
import com.schoolplus.office.repository.AnnouncementRepository;
import com.schoolplus.office.security.SecurityUser;
import com.schoolplus.office.services.AnnouncementService;
import com.schoolplus.office.web.exceptions.AnnouncementNotFoundException;
import com.schoolplus.office.web.mappers.AnnouncementMapper;
import com.schoolplus.office.web.models.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final AnnouncementMapper announcementMapper;

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:announcements') || hasAuthority('view:announcements'))")
    @Override
    public List<AnnouncementDto> getAnnouncements(Pageable pageable) {
        Page<Announcement> announcements = announcementRepository
                .findAll(pageable);

        return announcementMapper.announcementToAnnouncementDto(announcements.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:announcements') || hasAuthority('view:announcement'))")
    @Override
    public AnnouncementDto getAnnouncement(Long announcementId) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> {
                    log.warn("Announcement with given id does not exists [announcementId: {}]", announcementId);
                    throw new AnnouncementNotFoundException(ErrorDesc.ANNOUNCEMENT_NOT_FOUND.getDesc());
                });

        return announcementMapper.announcementToAnnouncementDto(announcement);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:announcements') || hasAuthority('write:announcement'))")
    @Override
    public AnnouncementDto createAnnouncement(CreatingAnnouncementDto creatingAnnouncement) {
        Announcement announcement = new Announcement();
        announcement.setAnnouncementTitle(creatingAnnouncement.getAnnouncementTitle());
        announcement.setAnnouncementDescription(creatingAnnouncement.getAnnouncementDescription());

        creatingAnnouncement.getAnnouncementChannels().forEach(_channel -> {
            AnnouncementChannel channel = AnnouncementChannel.valueOf(_channel);
            announcement.addChannel(channel);
        });

        Announcement savedAnnouncement = announcementRepository.save(announcement);

        log.info("The announcement has been created successfully [announcementId: {}, performedBy: {}]",
                savedAnnouncement.getId(),
                SecurityContextHolder.getContext().getAuthentication().getName());

        return announcementMapper.announcementToAnnouncementDto(savedAnnouncement);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:announcements') || hasAuthority('update:announcement'))")
    @Override
    public void updateAnnouncement(Long announcementId, UpdatingAnnouncementDto updatingAnnouncement) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> {
                    log.warn("Announcement with given id does not exists [announcementId: {}]", announcementId);
                    throw new AnnouncementNotFoundException(ErrorDesc.ANNOUNCEMENT_NOT_FOUND.getDesc());
                });

        if (updatingAnnouncement.getAnnouncementTitle() != null) {
            announcement.setAnnouncementTitle(updatingAnnouncement.getAnnouncementTitle());
        }

        if (updatingAnnouncement.getAnnouncementDescription() != null) {
            announcement.setAnnouncementDescription(updatingAnnouncement.getAnnouncementDescription());
        }

        if (updatingAnnouncement.getRemovedChanels() != null && updatingAnnouncement.getRemovedChanels().size() > 0) {
            updatingAnnouncement.getRemovedChanels().forEach(_channel -> {
                AnnouncementChannel channel = AnnouncementChannel.valueOf(_channel);
                announcement.removeChannel(channel);
            });
        }

        if (updatingAnnouncement.getAddedChannels() != null && updatingAnnouncement.getAddedChannels().size() > 0) {
            updatingAnnouncement.getAddedChannels().forEach(_channel -> {
                AnnouncementChannel channel = AnnouncementChannel.valueOf(_channel);
                announcement.addChannel(channel);
            });
        }

        announcementRepository.save(announcement);

        log.info("The announcement has been updated successfully [announcementId: {}, performedBy: {}]",
                announcementId,
                SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:announcements') || hasAuthority('update:announcement') || hasAuthority('write:announcement'))")
    @Override
    public List<AnnouncementImageDto> uploadImages(Long announcementId, MultipartFile[] images) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> {
                   log.warn("Announcement with given id does not exists [announcementId: {}]", announcementId);
                   throw new AnnouncementNotFoundException(ErrorDesc.ANNOUNCEMENT_NOT_FOUND.getDesc());
                });

        List<AnnouncementImageDto> announcementImages = new ArrayList<>();

        for (MultipartFile image : images) {
            announcement.addImage(RandomUtils.nextInt(1, 99) + image.getOriginalFilename());

            AnnouncementImageDto announcementImage = new AnnouncementImageDto();
            announcementImage.setAnnouncementId(announcementId);
            announcementImage.setImageUrl(image.getOriginalFilename());
            announcementImage.setImageSize(Integer.valueOf(String.valueOf(image.getSize()/1000000)));

            announcementImages.add(announcementImage);
        }

        announcementRepository.save(announcement);

        log.info("The announcement images has been added [announcementId: {}, performedBy: {}]",
                announcementId,
                SecurityContextHolder.getContext().getAuthentication().getName());

        return announcementImages;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:announcements') || hasAuthority('update:announcement') || hasAuthority('write:announcement'))")
    @Override
    public void deleteImages(Long announcementId, DeletingAnnouncementImageDto deletingAnnouncementImage) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> {
                    log.warn("Announcement with given id does not exists [announcementId: {}]", announcementId);
                    throw new AnnouncementNotFoundException(ErrorDesc.ANNOUNCEMENT_NOT_FOUND.getDesc());
                });

        deletingAnnouncementImage.getImageUrls().forEach(announcement::removeImage);

        announcementRepository.save(announcement);

        log.info("The announcement images has been deleted [announcementId: {}, performedBy: {}]",
                announcementId,
                SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:announcements') || hasAuthority('delete:announcement'))")
    @Override
    public void deleteAnnouncement(Long announcementId) {
        if (!announcementRepository.existsById(announcementId)) {
            log.warn("Announcement with given id does not exists [announcementId: {}]", announcementId);
            throw new AnnouncementNotFoundException(ErrorDesc.ANNOUNCEMENT_NOT_FOUND.getDesc());
        }

        announcementRepository.deleteById(announcementId);

        log.info("The announcement has been deleted successfully [announcementId: {}, performedBy: {}]",
                announcementId,
                SecurityContextHolder.getContext().getAuthentication().getName());
    }

}
