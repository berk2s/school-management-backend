package com.schoolplus.office.services;

import com.schoolplus.office.web.models.CreatingOrganizationDto;
import com.schoolplus.office.web.models.EditingOrganizationDto;
import com.schoolplus.office.web.models.OrganizationDto;

import java.util.List;

public interface OrganizationService {

    List<OrganizationDto> getOrganizations();

    OrganizationDto getOrganization(Long organizationId);

    OrganizationDto createOrganization(CreatingOrganizationDto creatingOrganization);

    void updateOrganization(Long organizationId, EditingOrganizationDto editingOrganization);

    void deleteOrganization(Long organizationId);

}
