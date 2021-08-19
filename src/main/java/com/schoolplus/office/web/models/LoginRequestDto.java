package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoginRequestDto {

    @NotEmpty
    @Size(max = 99)
    private String username;

    @NotEmpty
    @Size(max = 99)
    private String password;

    @NotEmpty
    @Size(min = 1, max = 99)
    private List<String> scopes = new ArrayList<>();

}
