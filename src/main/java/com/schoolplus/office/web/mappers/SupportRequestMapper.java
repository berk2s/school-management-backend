package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.domain.SupportRequest;
import com.schoolplus.office.web.models.SupportRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.List;

@Mapper(uses = {SupportThreadMapper.class, OrganizationMapper.class})
public interface SupportRequestMapper {

    @Mappings({
            @Mapping(source = "id", target = "supportRequestId"),
            @Mapping(source = "supportThreads", target = "supportThreads", qualifiedByName = "WithoutSupportRequest"),
            @Mapping(source = "organization", target = "organization"),
            @Mapping(source = "isLocked", target = "isLocked"),
            @Mapping(source = "createdAt", target = "createdAt"),
            @Mapping(source = "lastModifiedAt", target = "lastModifiedAt"),
    })
    SupportRequestDto supportRequestToSupportRequestDto(SupportRequest supportRequest);

    @Mappings({
            @Mapping(source = "id", target = "supportRequestId"),
            @Mapping(source = "supportThreads", target = "supportThreads", qualifiedByName = "WithoutSupportRequest"),
            @Mapping(source = "organization", target = "organization"),
            @Mapping(source = "isLocked", target = "isLocked"),
            @Mapping(source = "createdAt", target = "createdAt"),
            @Mapping(source = "lastModifiedAt", target = "lastModifiedAt"),
    })
    List<SupportRequestDto> supportRequestToSupportRequestDto(List<SupportRequest> supportRequest);

    @Named("WithoutThreads")
    @Mappings({
            @Mapping(source = "id", target = "supportRequestId"),
            @Mapping(source = "supportThreads", target = "supportThreads", ignore = true),
            @Mapping(source = "organization", target = "organization"),
            @Mapping(source = "isLocked", target = "isLocked"),
            @Mapping(source = "createdAt", target = "createdAt"),
            @Mapping(source = "lastModifiedAt", target = "lastModifiedAt"),
    })
    SupportRequestDto supportRequestToSupportRequestDtoWithoutThreads(SupportRequest supportRequest);

}
