package com.schoolplus.office.services.impl;

import com.schoolplus.office.annotations.CreatingEntity;
import com.schoolplus.office.annotations.DeletingEntity;
import com.schoolplus.office.annotations.ReadingEntity;
import com.schoolplus.office.annotations.UpdatingEntity;
import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.repository.OrganizationRepository;
import com.schoolplus.office.services.OrganizationService;
import com.schoolplus.office.web.exceptions.OrganizationNotFoundException;
import com.schoolplus.office.web.mappers.OrganizationMapper;
import com.schoolplus.office.web.models.*;
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

    @ReadingEntity(domain = TransactionDomain.ORGANIZATION, action = DomainAction.READ_ORGANIZATIONS, isList = true)
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:organizations') || hasAuthority('read:organizations'))")
    @Override
    public List<OrganizationDto> getOrganizations() {
        List<Organization> organizations = organizationRepository.findAll();

        return organizationMapper.organizationToOrganizationDto(organizations);
    }

    @ReadingEntity(domain = TransactionDomain.ORGANIZATION, action = DomainAction.READ_ORGANIZATION)
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

    @CreatingEntity(domain = TransactionDomain.ORGANIZATION, action = DomainAction.CREATE_ORGANIZATION)
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

    @UpdatingEntity(domain = TransactionDomain.ORGANIZATION, action = DomainAction.UPDATE_ORGANIZATION, idArg = "organizationId")
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

    @DeletingEntity(domain = TransactionDomain.ORGANIZATION, action = DomainAction.DELETE_ORGANIZATION, idArg = "organizationId")
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
