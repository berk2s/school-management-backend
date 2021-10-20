package com.schoolplus.office.services;

import com.schoolplus.office.web.models.UserDto;

import java.util.UUID;

public interface UserInfoService {

    UserDto getUserInfo(UUID userId);

}
