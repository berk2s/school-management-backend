package com.schoolplus.office.services;

import com.schoolplus.office.web.models.EditingUserInformationDto;

import java.util.UUID;

public interface UserInformationService {

    void editProfile(UUID userId, EditingUserInformationDto editingUserInformationDto);

}
