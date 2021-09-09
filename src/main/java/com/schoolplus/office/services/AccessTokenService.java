package com.schoolplus.office.services;

import com.schoolplus.office.security.SecurityUser;
import com.schoolplus.office.web.models.AccessTokenCommand;
import com.schoolplus.office.web.models.TokenRequestDto;
import com.schoolplus.office.web.models.TokenResponseDto;

public interface AccessTokenService {

    String createToken(AccessTokenCommand accessTokenCommand);

    TokenResponseDto checkToken(TokenRequestDto tokenRequest);

}
