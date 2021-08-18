package com.schoolplus.office.services;

import com.schoolplus.office.web.models.LoginRequestDto;
import com.schoolplus.office.web.models.LoginResponseDto;

public interface LoginService {

    LoginResponseDto authenticate(LoginRequestDto loginRequest);

}
