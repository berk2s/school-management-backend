package com.schoolplus.office.web.models;

import com.schoolplus.office.annotations.Logable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SupportThreadDto {

    @Logable(type = LogableType.ID)
    private Long supportThreadId;

    private String threadMessage;

    private UserDto user;

    private SupportRequestDto supportRequest;

    private Timestamp createdAt;

    private Timestamp lastModifiedAt;

}
