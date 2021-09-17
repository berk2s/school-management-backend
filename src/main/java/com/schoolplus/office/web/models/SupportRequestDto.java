package com.schoolplus.office.web.models;

import com.schoolplus.office.annotations.Logable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SupportRequestDto {

    @Logable(type = LogableType.ID)
    private Long supportRequestId;

    private List<SupportThreadDto> supportThreads = new ArrayList<>();

    private OrganizationDto organization;

    private Boolean isSeen;

    private Boolean isLocked;

    private Boolean isAnonymous;

    private Timestamp createdAt;

    private Timestamp lastModifiedAt;

}
