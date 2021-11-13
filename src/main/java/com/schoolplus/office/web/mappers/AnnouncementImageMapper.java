package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.AnnouncementImage;
import com.schoolplus.office.web.models.AnnouncementImageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public interface AnnouncementImageMapper {

    @Mappings({
            @Mapping(target = "imageUrl", expression = "java( announcementImage.getPath() + '/' + announcementImage.getFileName() )"),
            @Mapping(target = "imageSize", source = "imageSize")
    })
    AnnouncementImageDto announcementImageToAnnouncementImageDto(AnnouncementImage announcementImage);

}
