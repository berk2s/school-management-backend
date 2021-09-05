package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AnnouncementImageDto {

    private Long announcementId;

    private String imageUrl;

    private Integer imageSize;

}
