package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SupportThreadDto {

    private Long supportThreadId;

    private String threadMessage;

    private UserDto user;

    private SupportRequestDto supportRequest;

    private Timestamp createdAt;

    private Timestamp lastModifiedAt;

}
