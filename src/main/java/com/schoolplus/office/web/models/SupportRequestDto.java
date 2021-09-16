package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SupportRequestDto {

    private Long supportRequestId;

    private List<SupportThreadDto> supportThreads = new ArrayList<>();

    private OrganizationDto organization;

    private Boolean isSeen;

    private Boolean isLocked;

    private Boolean isAnonymous;

    private Timestamp createdAt;

    private Timestamp lastModifiedAt;

}
