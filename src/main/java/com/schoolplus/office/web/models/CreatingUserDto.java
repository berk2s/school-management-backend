package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public abstract class CreatingUserDto {

    @Size(min = 3, max = 99)
    @NotEmpty
    private String username;

    @Size(min = 6, max = 99)
    @NotEmpty
    private String password;

    @Size(min = 2, max = 99)
    @NotEmpty
    private String firstName;

    @Size(min = 2, max = 99)
    @NotEmpty
    private String lastName;

    @Size(min = 11, max = 12)
    @NotEmpty
    private String phoneNumber;

    @Size(max = 99)
    private String email;

//    @Size(min = 1, max = 99)
//    @NotNull
    private List<Long> authorities;

//    @Size(min = 1, max = 99)
//    @NotNull
    private List<Long> roles;

//    @NotNull
    private Boolean isEnabled;

//    @NotNull
    private Boolean isAccountNonExpired;

//    @NotNull
    private Boolean isAccountNonLocked;

//    @NotNull
    private Boolean isCredentialsNonExpired;

    @NotNull
    private Long organizationId;

}
