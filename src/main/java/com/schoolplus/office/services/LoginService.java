package com.schoolplus.office.services;

import com.schoolplus.office.web.models.LoginRequestDto;
import com.schoolplus.office.web.models.TokenResponseDto;

public interface LoginService {

    TokenResponseDto authenticate(LoginRequestDto loginRequest);

}
