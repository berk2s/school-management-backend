package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.web.models.OrganizationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface OrganizationMapper {

    @Mapping(source = "id", target = "organizationId")
    OrganizationDto organizationToOrganizationDto(Organization organization);

    @Mapping(source = "id", target = "organizationId")
    List<OrganizationDto> organizationToOrganizationDto(List<Organization> organization);

}
