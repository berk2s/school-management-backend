package com.schoolplus.office.services;

import com.schoolplus.office.security.SecurityUser;
import com.schoolplus.office.web.models.AccessTokenCommand;

public interface AccessTokenService {

    String createToken(AccessTokenCommand accessTokenCommand);

}
