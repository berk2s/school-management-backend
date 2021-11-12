package com.schoolplus.office.services.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.schoolplus.office.annotations.CreatingEntity;
import com.schoolplus.office.annotations.DeletingEntity;
import com.schoolplus.office.annotations.ReadingEntity;
import com.schoolplus.office.annotations.UpdatingEntity;
import com.schoolplus.office.config.BucketFolder;
import com.schoolplus.office.config.BucketName;
import com.schoolplus.office.domain.Announcement;
import com.schoolplus.office.domain.AnnouncementImage;
import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.repository.AnnouncementRepository;
import com.schoolplus.office.repository.OrganizationRepository;
import com.schoolplus.office.services.AnnouncementService;
import com.schoolplus.office.services.FileService;
import com.schoolplus.office.web.exceptions.AnnouncementNotFoundException;
import com.schoolplus.office.web.exceptions.FileUploadingException;
import com.schoolplus.office.web.exceptions.OrganizationNotFoundException;
import com.schoolplus.office.web.mappers.AnnouncementMapper;
import com.schoolplus.office.web.models.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.apache.http.entity.ContentType.IMAGE_JPEG;
import static org.apache.http.entity.ContentType.IMAGE_PNG;

@Slf4j
@RequiredArgsConstructor
@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final OrganizationRepository organizationRepository;
    private final AnnouncementMapper announcementMapper;
    private final FileService fileService;

    @ReadingEntity(domain = TransactionDomain.ANNOUNCEMENT, action = DomainAction.READ_ANNOUNCEMENTS, isList = true)
    @PreAuthorize("hasRole('ROLE_ADMIN') || hasAuthority('manage:announcements') || hasAuthority('view:announcements')")
    @Override
    public List<AnnouncementDto> getAnnouncements(Pageable pageable) {
        Page<Announcement> announcements = announcementRepository
                .findAll(pageable);

        return announcementMapper.announcementToAnnouncementDto(announcements.getContent());
    }

    @ReadingEntity(domain = TransactionDomain.ANNOUNCEMENT, action = DomainAction.READ_ANNOUNCEMENTS, isList = true)
    @PreAuthorize("hasRole('ROLE_ADMIN') || hasAuthority('manage:announcements') || hasAuthority('view:announcements')")
    @Override
    public Page<AnnouncementDto> getAnnouncementsByOrganization(Long organizationId,
                                                                Pageable pageable,
                                                                String search) {

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists [organizationId: {}]", organizationId);
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        Page<Announcement> announcements;

        if(StringUtils.isEmpty(search)) {
            announcements = announcementRepository.findAllByOrganization(organization, pageable);
        } else {
            announcements = announcementRepository
                    .findAllByOrganizationAndAnnouncementTitleStartsWith(organization, search, pageable);
        }

        return new PageImpl<>(
                announcementMapper.announcementToAnnouncementDto(announcements.getContent()),
                pageable,
                announcements.getTotalElements());
    }

    @ReadingEntity(domain = TransactionDomain.ANNOUNCEMENT, action = DomainAction.READ_ANNOUNCEMENT)
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

    @CreatingEntity(domain = TransactionDomain.ANNOUNCEMENT, action = DomainAction.CREATE_ANNOUNCEMENT)
    @PreAuthorize("hasRole('ROLE_ADMIN') || (hasAuthority('manage:announcements') || hasAuthority('write:announcement'))")
    @Override
    public AnnouncementDto createAnnouncement(CreatingAnnouncementDto creatingAnnouncement) {
        Announcement announcement = new Announcement();
        announcement.setAnnouncementTitle(creatingAnnouncement.getAnnouncementTitle());
        announcement.setAnnouncementDescription(creatingAnnouncement.getAnnouncementDescription());
        announcement.setAnnouncementStatus(creatingAnnouncement.getAnnouncementStatus());

        Organization organization = organizationRepository.findById(creatingAnnouncement.getOrganizationId())
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists [organizationId: {}]", creatingAnnouncement.getOrganizationId());
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        announcement.setOrganization(organization);

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

    @UpdatingEntity(domain = TransactionDomain.ANNOUNCEMENT, action = DomainAction.UPDATE_ANNOUNCEMENT, idArg = "announcementId")
    @PreAuthorize("hasRole('ROLE_ADMIN') || (hasAuthority('manage:announcements') || hasAuthority('update:announcement'))")
    @Override
    public void updateAnnouncement(Long announcementId, EditingAnnouncementDto updatingAnnouncement) {
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

        if (updatingAnnouncement.getAnnouncementStatus() != null) {
            announcement.setAnnouncementStatus(updatingAnnouncement.getAnnouncementStatus());
        }

        if (updatingAnnouncement.getOrganizationId() != null) {
            Organization organization = organizationRepository.findById(updatingAnnouncement.getOrganizationId())
                    .orElseThrow(() -> {
                        log.warn("Organization with given id does not exists [organizationId: {}]", updatingAnnouncement.getOrganizationId());
                        throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                    });

            announcement.setOrganization(organization);
        }

        if (updatingAnnouncement.getRemovedChannels() != null && updatingAnnouncement.getRemovedChannels().size() > 0) {
            updatingAnnouncement.getRemovedChannels().forEach(_channel -> {
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

    @UpdatingEntity(domain = TransactionDomain.ANNOUNCEMENT, action = DomainAction.UPLOAD_ANNOUNCEMENT_IMAGE, idArg = "announcementId", isList = true)
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
            if (!Arrays.asList(IMAGE_PNG.getMimeType(),
                    IMAGE_JPEG.getMimeType()).contains(image.getContentType())) {
                log.warn("Invalid content type [contentType: {}]", image.getContentType());
                throw new FileUploadingException(ErrorDesc.INVALID_CONTENT_TYPE.getDesc());
            }

            String imageName =
                    RandomStringUtils.random(20, true, false) + "." + getExtension(Objects.requireNonNull(image.getOriginalFilename()));

            Map<String, String> metadata = new HashMap<>();
            metadata.put("originalName", image.getOriginalFilename());
            metadata.put("size", String.valueOf(image.getSize()));

            try {
                fileService.upload(BucketName.IMAGE.getBucketName() + "/" + BucketFolder.ANNOUNCEMENTS.getPath(), imageName, Optional.of(metadata), image.getInputStream(), image.getSize());
            } catch (IOException e) {
                log.warn("Failed while getting input stream {}", e.getMessage());
                throw new FileUploadingException(ErrorDesc.FAILED_IMAGE_UPLOAD.getDesc());
            }

            String path = String.format("%s/%s", BucketFolder.ANNOUNCEMENTS, imageName);

            AnnouncementImage announcementImage = new AnnouncementImage();
            announcementImage.setPath(BucketFolder.ANNOUNCEMENTS.getPath());
            announcementImage.setFileName(imageName);
            announcementImage.setImageSize(image.getSize());

            announcement.addImage(announcementImage);

            AnnouncementImageDto announcementImageDto = new AnnouncementImageDto();
            announcementImageDto.setAnnouncementId(announcementId);
            announcementImageDto.setImageUrl(path);
            announcementImageDto.setImageSize(image.getSize() / 1000000);

            announcementImages.add(announcementImageDto);
        }

        announcementRepository.save(announcement);

        log.info("The announcement images has been added [announcementId: {}, performedBy: {}]",
                announcementId,
                SecurityContextHolder.getContext().getAuthentication().getName());

        return announcementImages;
    }

    @DeletingEntity(domain = TransactionDomain.ANNOUNCEMENT, action = DomainAction.DELETE_ANNOUNCEMENT_IMAGE, idArg = "announcementId")
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:announcements') || hasAuthority('update:announcement') || hasAuthority('write:announcement'))")
    @Override
    public void deleteImages(Long announcementId, DeletingAnnouncementImageDto deletingAnnouncementImage) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> {
                    log.warn("Announcement with given id does not exists [announcementId: {}]", announcementId);
                    throw new AnnouncementNotFoundException(ErrorDesc.ANNOUNCEMENT_NOT_FOUND.getDesc());
                });


        deletingAnnouncementImage.getImageUrls().forEach(deletedImagesName -> {
            Optional<AnnouncementImage> optionalAnnouncementImage
                    = announcement.getAnnouncementImages().stream()
                    .filter(i -> i.getFileName().equals(deletedImagesName))
                    .findFirst();

            if (optionalAnnouncementImage.isPresent()) {
                announcement.removeImage(optionalAnnouncementImage.get());
            }
        });

        announcementRepository.save(announcement);

        log.info("The announcement images has been deleted [announcementId: {}, performedBy: {}]",
                announcementId,
                SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @DeletingEntity(domain = TransactionDomain.ANNOUNCEMENT, action = DomainAction.DELETE_ANNOUNCEMENT, idArg = "announcementId")
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

    private String getExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

}
