package com.schoolplus.office.web.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private String userId;

    private String username;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String email;

    private OrganizationDto organization;

    private List<String> authorities;

    private List<String> roles;

    private Boolean isEnabled;

    private Boolean isAccountNonExpired;

    private Boolean isAccountNonLocked;

    private Boolean isCredentialsNonExpired;

    private Timestamp createdAt;

    private Timestamp lastModifiedAt;

}
