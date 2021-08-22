package com.schoolplus.office.web.models;

import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {

    private String userId;

    private String username;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String email;

    private List<String> authorities;

    private List<String> roles;

    private Boolean isEnabled;

    private Boolean isAccountNonExpired;

    private Boolean isAccountNonLocked;

    private Boolean isCredentialsNonExpired;

    private Timestamp createdAt;

    private Timestamp lastModifiedAt;

}
