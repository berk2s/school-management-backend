package com.schoolplus.office.services.impl;

import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.repository.OrganizationRepository;
import com.schoolplus.office.services.OrganizationService;
import com.schoolplus.office.web.exceptions.OrganizationNotFoundException;
import com.schoolplus.office.web.mappers.OrganizationMapper;
import com.schoolplus.office.web.models.CreatingOrganizationDto;
import com.schoolplus.office.web.models.EditingOrganizationDto;
import com.schoolplus.office.web.models.ErrorDesc;
import com.schoolplus.office.web.models.OrganizationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:organizations') || hasAuthority('read:organizations'))")
    @Override
    public List<OrganizationDto> getOrganizations() {
        List<Organization> organizations = organizationRepository.findAll();

        return organizationMapper.organizationToOrganizationDto(organizations);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:organizations') || hasAuthority('view:organization'))")
    @Override
    public OrganizationDto getOrganization(Long organizationId) {
        Organization organization = organizationRepository
                .findById(organizationId)
                .orElseThrow(() -> {
                   log.warn("Organization with given id does not exists [organizationId: {}]", organizationId);
                   throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        return organizationMapper.organizationToOrganizationDto(organization);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:organizations') || hasAuthority('create:organization'))")
    @Override
    public OrganizationDto createOrganization(CreatingOrganizationDto creatingOrganization) {
        Organization organization = new Organization();
        organization.setOrganizationName(creatingOrganization.getOrganizationName());

        log.info("The organization has been created successfully [organizationId: {}, performedBy: {}]",
                organization.getId(),
                SecurityContextHolder.getContext().getAuthentication().getName());

        return organizationMapper.organizationToOrganizationDto(organizationRepository.save(organization));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:organizations') || hasAuthority('update:organization'))")
    @Override
    public void updateOrganization(Long organizationId, EditingOrganizationDto editingOrganization) {
        Organization organization = organizationRepository
                .findById(organizationId)
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists [organizationId: {}]", organizationId);
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        if (editingOrganization.getOrganizationName() != null) {
            organization.setOrganizationName(editingOrganization.getOrganizationName());
        }

        organizationRepository.save(organization);

        log.info("The organization has been created successfully [organizationId: {}, performedBy: {}]",
                organization.getId(),
                SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:organizations') || hasAuthority('delete:organization'))")
    @Override
    public void deleteOrganization(Long organizationId) {
        if (!organizationRepository.existsById(organizationId)) {
            log.warn("Organization with given id does not exists [organizationId: {}]", organizationId);
            throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
        }

        organizationRepository.deleteById(organizationId);
    }

}
