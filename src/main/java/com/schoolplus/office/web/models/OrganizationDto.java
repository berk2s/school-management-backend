package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrganizationDto {

    private Long organizationId;

    private String organizationName;

    private Timestamp createdAt;

    private Timestamp lastModifiedAt;

}
