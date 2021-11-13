package com.schoolplus.office.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BucketFolder {
    ANNOUNCEMENTS("announcements");
    private final String path;
}
