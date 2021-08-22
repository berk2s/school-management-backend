package com.schoolplus.office.services;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.schoolplus.office.web.models.TokenCommand;

public interface JwtService {

    SignedJWT createJwt(TokenCommand tokenCommand);

    SignedJWT signJWT(JWTClaimsSet jwtClaimsSet);

    JWTClaimsSet parseAndValidate(String token);

    boolean validate(String token);

}
