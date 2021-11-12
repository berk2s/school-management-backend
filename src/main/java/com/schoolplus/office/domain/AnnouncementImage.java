package com.schoolplus.office.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Getter
@Setter
@Embeddable
public class AnnouncementImage {

    private String path;

    private String fileName;

    private Long imageSize;

}
