package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EditingUserDto {

    private String username;

    private String password;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String email;

    private Long organizationId;

    @Size(max = 99)
    private List<Long> newAuthorities;

    @Size(max = 99)
    private List<Long> deletedAuthorities;

    @Size(max = 99)
    private List<Long> newRoles;

    @Size(max = 99)
    private List<Long> deletedRoles;

    private Boolean isAccountNonExpired;

    private Boolean isAccountNonLocked;

    private Boolean isCredentialsNonExpired;

    private Boolean isEnabled;


}
