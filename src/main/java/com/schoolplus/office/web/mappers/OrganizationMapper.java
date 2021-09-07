package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.web.models.OrganizationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper
public interface OrganizationMapper {

    @Mappings({
            @Mapping(target = "organizationId", source = "id")
    })
    OrganizationDto organizationToOrganizationDto(Organization organization);

    @Mappings({
            @Mapping(target = "organizationId", source = "id")
    })
    List<OrganizationDto> organizationToOrganizationDto(List<Organization> organization);

}
