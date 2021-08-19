package com.schoolplus.office.services;

import com.schoolplus.office.web.models.RefreshTokenCommand;

public interface RefreshTokenService {

    String createToken(RefreshTokenCommand refreshTokenCommand);

}
