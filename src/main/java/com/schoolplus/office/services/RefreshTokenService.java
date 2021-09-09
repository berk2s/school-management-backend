package com.schoolplus.office.services;

import com.schoolplus.office.web.models.RefreshTokenCommand;
import com.schoolplus.office.web.models.TokenRequestDto;
import com.schoolplus.office.web.models.TokenResponseDto;

public interface RefreshTokenService {

    String createToken(RefreshTokenCommand refreshTokenCommand);
    TokenResponseDto createToken(TokenRequestDto tokenRequest);

}
