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

    @NotEmpty
    private String username;

    @NotEmpty
    private String password;

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    @NotEmpty
    private String phoneNumber;

    @NotEmpty
    private String email;

    @Size(min = 1, max = 99)
    @NotNull
    private List<Long> authorities;

    @Size(min = 1, max = 99)
    @NotNull
    private List<Long> roles;

    @NotNull
    private Boolean isEnabled;

    @NotNull
    private Boolean isAccountNonExpired;

    @NotNull
    private Boolean isAccountNonLocked;

    @NotNull
    private Boolean isCredentialsNonExpired;


}
