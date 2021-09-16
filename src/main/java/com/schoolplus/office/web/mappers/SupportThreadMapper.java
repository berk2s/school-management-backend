package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.SupportThread;
import com.schoolplus.office.web.models.SupportThreadDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.List;

@Mapper(uses = {SupportRequestMapper.class, UserMapper.class})
public interface SupportThreadMapper {

    @Mappings({
            @Mapping(source = "id", target = "supportThreadId"),
            @Mapping(source = "supportRequest", target = "supportRequest", qualifiedByName = "WithoutThreads"),
            @Mapping(source = "createdAt", target = "createdAt"),
            @Mapping(source = "lastModifiedAt", target = "lastModifiedAt"),
    })
    SupportThreadDto supportThreadToSupportThreadDto(SupportThread supportThread);

    @Mappings({
            @Mapping(source = "id", target = "supportThreadId"),
            @Mapping(source = "supportRequest", target = "supportRequest", qualifiedByName = "WithoutThreads"),
            @Mapping(source = "createdAt", target = "createdAt"),
            @Mapping(source = "lastModifiedAt", target = "lastModifiedAt"),
    })
    List<SupportThreadDto> supportThreadToSupportThreadDto(List<SupportThread> supportThread);

    @Named("WithoutSupportRequest")
    @Mappings({
            @Mapping(source = "id", target = "supportThreadId"),
            @Mapping(source = "supportRequest", target = "supportRequest", ignore = true),
            @Mapping(source = "createdAt", target = "createdAt"),
            @Mapping(source = "lastModifiedAt", target = "lastModifiedAt"),
    })
    SupportThreadDto supportThreadToSupportThreadDtoWithoutRequests(SupportThread supportThread);
}
