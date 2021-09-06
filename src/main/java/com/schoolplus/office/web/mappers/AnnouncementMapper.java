package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.Announcement;
import com.schoolplus.office.web.models.AnnouncementDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper
public interface AnnouncementMapper {

    @Mappings({
            @Mapping(target = "announcementId", source = "id"),
            @Mapping(target = "organization", source = "organization"),
    })
    AnnouncementDto announcementToAnnouncementDto(Announcement announcement);

    @Mappings({
            @Mapping(target = "announcementId", source = "id"),
            @Mapping(target = "organiaztion", source = "organization"),
    })
    List<AnnouncementDto> announcementToAnnouncementDto(List<Announcement> announcement);

}
