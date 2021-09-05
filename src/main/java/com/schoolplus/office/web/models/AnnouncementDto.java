package com.schoolplus.office.web.models;

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
public class AnnouncementDto {

    private Long announcementId;

    private List<String> announcementImages = new ArrayList<>();

    private String announcementTitle;

    private String announcementDescription;

    private List<AnnouncementChannel> announcementChannels;

    private Timestamp createdAt;

    private Timestamp lastModifiedAt;

}
