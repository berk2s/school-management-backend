package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditingSupportRequestDto {

    private Set<Long> removedSupportThreads = new HashSet<>();

    private Long organizationId;

    private Boolean isSeen;

    private Boolean isLocked;

}
