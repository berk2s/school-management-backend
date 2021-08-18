package com.schoolplus.office.web.models;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Builder
public class LoginRequestDto {

    @NotEmpty
    @Size(max = 99)
    private String username;

    @NotEmpty
    @Size(max = 99)
    private String password;

}
