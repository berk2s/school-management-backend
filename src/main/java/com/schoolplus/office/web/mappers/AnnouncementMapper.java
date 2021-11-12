package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.Announcement;
import com.schoolplus.office.web.models.AnnouncementDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(uses = {AnnouncementImageMapper.class})
public interface AnnouncementMapper {

    @Mappings({
            @Mapping(target = "announcementId", source = "id"),
            @Mapping(target = "organization", source = "organization"),
            @Mapping(target = "announcementImages", source = "announcementImages")
    })
    AnnouncementDto announcementToAnnouncementDto(Announcement announcement);

    @Mappings({
            @Mapping(target = "announcementId", source = "id"),
            @Mapping(target = "organiaztion", source = "organization"),
            @Mapping(target = "announcementImages", source = "announcementImages")
    })
    List<AnnouncementDto> announcementToAnnouncementDto(List<Announcement> announcement);

}
