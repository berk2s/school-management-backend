package com.schoolplus.office.services;

import com.schoolplus.office.web.models.CreatingSupportResponseDto;
import com.schoolplus.office.web.models.EditingSupportRequestDto;
import com.schoolplus.office.web.models.SupportRequestDto;
import com.schoolplus.office.web.models.SupportThreadDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface SupportService {

    SupportRequestDto getSupportRequest(Long supportRequestId);

    List<SupportRequestDto> getSupportRequestByOrganization(Long organizationId, Pageable pageable);

    List<SupportRequestDto> getSupportRequestByOrganizationAndUnanswered(Long organizationId, Pageable pageable);

    List<SupportRequestDto> getSupportRequestByOrganizationAndAnswered(Long organizationId, Pageable pageable);

    List<SupportRequestDto> getSupportRequestByOrganizationAndAnonymous(Long organizationId, Pageable pageable);

    List<SupportRequestDto> getSupportRequestByOrganizationAndNamed(Long organizationId, Pageable pageable);

    List<SupportRequestDto> getSupportRequestByOrganizationAndUser(Long organizationId, UUID userId, Pageable pageable);

    SupportThreadDto createSupportResponse(CreatingSupportResponseDto creatingSupportResponse);

    void updateSupportRequest(Long supportRequestId, EditingSupportRequestDto editingSupportRequest);

    void deleteSupportRequest(Long supportRequestId);

    void deleteSupportThread(Long supportThreadId);

}
