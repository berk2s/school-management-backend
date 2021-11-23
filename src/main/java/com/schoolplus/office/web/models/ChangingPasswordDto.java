package com.schoolplus.office.web.models;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ChangingPasswordDto {

    @NotNull
    private String currentPassword;

    @NotNull
    private String newPassword;

    @NotNull
    private String newPasswordConfirm;

}
