package com.schoolplus.office.services.impl;

import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.domain.SupportRequest;
import com.schoolplus.office.domain.SupportThread;
import com.schoolplus.office.domain.User;
import com.schoolplus.office.repository.OrganizationRepository;
import com.schoolplus.office.repository.SupportRequestRepository;
import com.schoolplus.office.repository.SupportThreadRepository;
import com.schoolplus.office.repository.UserRepository;
import com.schoolplus.office.services.SupportService;
import com.schoolplus.office.web.exceptions.OrganizationNotFoundException;
import com.schoolplus.office.web.exceptions.SupportRequestNotFoundException;
import com.schoolplus.office.web.exceptions.SupportThreadNotFoundException;
import com.schoolplus.office.web.exceptions.UserNotFoundException;
import com.schoolplus.office.web.mappers.SupportRequestMapper;
import com.schoolplus.office.web.mappers.SupportThreadMapper;
import com.schoolplus.office.web.models.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class SupportServiceImpl implements SupportService {

    private final SupportRequestRepository supportRequestRepository;
    private final SupportThreadRepository supportThreadRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final SupportRequestMapper supportRequestMapper;
    private final SupportThreadMapper supportThreadMapper;

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:supports') || hasAuthority('read:support'))")
    @Override
    public SupportRequestDto getSupportRequest(Long supportRequestId) {
        SupportRequest supportRequest = supportRequestRepository.findById(supportRequestId)
                .orElseThrow(() -> {
                    log.warn("Support Request with given id does not exists [supportRequestId: {}]", supportRequestId);
                    throw new SupportRequestNotFoundException(ErrorDesc.SUPPORT_REQUEST_NOT_FOUND.getDesc());
                });

        return supportRequestMapper.supportRequestToSupportRequestDto(supportRequest);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:supports') || hasAuthority('read:support'))")
    @Override
    public List<SupportRequestDto> getSupportRequestByOrganization(Long organizationId, Pageable pageable) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists [organizationId: {}]", organizationId);
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        Page<SupportRequest> supportRequests
                = supportRequestRepository.findAllByOrganization(organization, pageable);

        return supportRequestMapper.supportRequestToSupportRequestDto(supportRequests.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:supports') || hasAuthority('read:support'))")
    @Override
    public List<SupportRequestDto> getSupportRequestByOrganizationAndUnanswered(Long organizationId, Pageable pageable) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists [organizationId: {}]", organizationId);
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        Page<SupportRequest> supportRequests
                = supportRequestRepository.findAllByOrganizationAndIsSeen(organization, false, pageable);

        return supportRequestMapper.supportRequestToSupportRequestDto(supportRequests.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:supports') || hasAuthority('read:support'))")
    @Override
    public List<SupportRequestDto> getSupportRequestByOrganizationAndAnswered(Long organizationId, Pageable pageable) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists [organizationId: {}]", organizationId);
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        Page<SupportRequest> supportRequests
                = supportRequestRepository.findAllByOrganizationAndIsSeen(organization, true, pageable);

        return supportRequestMapper.supportRequestToSupportRequestDto(supportRequests.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:supports') || hasAuthority('read:support'))")
    @Override
    public List<SupportRequestDto> getSupportRequestByOrganizationAndAnonymous(Long organizationId, Pageable pageable) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists [organizationId: {}]", organizationId);
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        Page<SupportRequest> supportRequests
                = supportRequestRepository.findAllByOrganizationAndIsAnonymous(organization, true, pageable);

        return supportRequestMapper.supportRequestToSupportRequestDto(supportRequests.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:supports') || hasAuthority('read:support'))")
    @Override
    public List<SupportRequestDto> getSupportRequestByOrganizationAndNamed(Long organizationId, Pageable pageable) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists [organizationId: {}]", organizationId);
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        Page<SupportRequest> supportRequests
                = supportRequestRepository.findAllByOrganizationAndIsAnonymous(organization, false, pageable);

        return supportRequestMapper.supportRequestToSupportRequestDto(supportRequests.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:supports') || hasAuthority('write:support'))")
    @Override
    public List<SupportRequestDto> getSupportRequestByOrganizationAndUser(Long organizationId, UUID userId, Pageable pageable) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists [organizationId: {}]", organizationId);
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User with given id does not exists [userId: {}]", userId);
                    throw new UserNotFoundException(ErrorDesc.USER_NOT_FOUND.getDesc());
                });

        Page<SupportRequest> supportRequests
                = supportRequestRepository.findAllByOrganizationAndIsAnonymousAndSupportThreads_User(organization, false, user, pageable);

        return supportRequestMapper.supportRequestToSupportRequestDto(supportRequests.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:supports') || hasAuthority('write:support'))")
    @Override
    public SupportThreadDto createSupportResponse(CreatingSupportResponseDto creatingSupportResponse) {
        SupportThread supportThread = new SupportThread();
        supportThread.setThreadMessage(creatingSupportResponse.getResponseMessage());

        User user = userRepository.findById(UUID.fromString(creatingSupportResponse.getUserId()))
                .orElseThrow(() -> {
                    log.warn("User with given id does not exists [userId: {}]",
                            creatingSupportResponse.getUserId());
                    throw new UserNotFoundException(ErrorDesc.USER_NOT_FOUND.getDesc());
                });

        supportThread.setUser(user);

        SupportRequest supportRequest = supportRequestRepository.findById(creatingSupportResponse.getSupportId())
                .orElseThrow(() -> {
                    log.warn("Support Request with given id does not exists [supportRequestId: {}]",
                            creatingSupportResponse.getSupportId());
                    throw new SupportRequestNotFoundException(ErrorDesc.SUPPORT_REQUEST_NOT_FOUND.getDesc());
                });

        supportRequest.addThread(supportThread);

        supportThreadRepository.save(supportThread);

        log.info("The Support Thread has been created successfully [supportThreadId: {}, supportRequestId: {}, performedBy: {}]",
                supportThread.getId(), supportRequest.getId(), SecurityContextHolder.getContext().getAuthentication().getName());

        return supportThreadMapper.supportThreadToSupportThreadDto(supportThread);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:supports') || hasAuthority('edit:support'))")
    @Override
    public void updateSupportRequest(Long supportRequestId, EditingSupportRequestDto editingSupportRequest) {
        SupportRequest supportRequest = supportRequestRepository.findById(supportRequestId)
                .orElseThrow(() -> {
                    log.warn("Support Request with given id does not exists [supportRequestId: {}]", supportRequestId);
                    throw new SupportRequestNotFoundException(ErrorDesc.SUPPORT_REQUEST_NOT_FOUND.getDesc());
                });

        if (editingSupportRequest.getIsSeen() != null) {
            supportRequest.setIsSeen(editingSupportRequest.getIsSeen());
        }

        if (editingSupportRequest.getIsLocked() != null) {
            supportRequest.setIsLocked(editingSupportRequest.getIsLocked());
        }

        if (editingSupportRequest.getOrganizationId() != null) {
            Organization organization = organizationRepository.findById(editingSupportRequest.getOrganizationId())
                    .orElseThrow(() -> {
                        log.warn("Organization with given id does not exists [organizationId: {}]", editingSupportRequest.getOrganizationId());
                        throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                    });

            supportRequest.setOrganization(organization);
        }

        if (editingSupportRequest.getRemovedSupportThreads() != null
                && editingSupportRequest.getRemovedSupportThreads().size() > 0) {
            editingSupportRequest.getRemovedSupportThreads().forEach(removedSupportThreadId -> {
                supportThreadRepository.findById(removedSupportThreadId)
                        .ifPresent(supportRequest::removeThread);
            });
        }

        supportRequestRepository.save(supportRequest);

        log.info("The Support Request has been updated successfully [supportRequestId: {}, performedBy: {}]",
                supportRequestId, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:supports') || hasAuthority('delete:support'))")
    @Override
    public void deleteSupportRequest(Long supportRequestId) {
        if (!supportRequestRepository.existsById(supportRequestId)) {
            log.warn("Support request with given id does not exists [supportRequestId: {}]", supportRequestId);
            throw new SupportRequestNotFoundException(ErrorDesc.SUPPORT_REQUEST_NOT_FOUND.getDesc());
        }

        supportRequestRepository.deleteById(supportRequestId);

        log.info("The Support Request has been deleted successfully [supportRequestId: {}, performedBy: {}]", supportRequestId,
                SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:supports') || hasAuthority('delete:support'))")
    @Override
    public void deleteSupportThread(Long supportThreadId) {
        if (!supportThreadRepository.existsById(supportThreadId)) {
            log.warn("Support Thread with given id does not exists [supportThreadId: {}]", supportThreadId);
            throw new SupportThreadNotFoundException(ErrorDesc.SUPPORT_THREAD_NOT_FOUND.getDesc());
        }

        supportThreadRepository.deleteById(supportThreadId);

        log.info("The Support Thread has been deleted successfully [supportThreadId: {}, performedBy: {}]", supportThreadId,
                SecurityContextHolder.getContext().getAuthentication().getName());
    }

}
