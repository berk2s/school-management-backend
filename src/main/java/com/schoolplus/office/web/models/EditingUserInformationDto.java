package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditingUserInformationDto {

    private String username;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String email;

}
