package com.schoolplus.office.web.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.schoolplus.office.annotations.Logable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnnouncementDto {

    @Logable(type = LogableType.ID)
    private Long announcementId;

    private List<AnnouncementImageDto> announcementImages = new ArrayList<>();

    private String announcementTitle;

    private String announcementDescription;

    private Boolean announcementStatus;

    private List<AnnouncementChannel> announcementChannels;

    private OrganizationDto organization;

    private Timestamp createdAt;

    private Timestamp lastModifiedAt;

}
