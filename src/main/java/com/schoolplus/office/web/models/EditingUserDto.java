package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EditingUserDto {

    private String username;

    private String password;

    private List<Long> newAuthorities;

    private List<Long> deletedAuthorities;

    private List<Long> newRoles;

    private List<Long> deletedRoles;

    private Boolean isAccountNonExpired;

    private Boolean isAccountNonLocked;

    private Boolean isCredentialsNonExpired;

    private Boolean isEnabled;

}
