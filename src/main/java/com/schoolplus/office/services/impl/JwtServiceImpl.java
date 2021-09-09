package com.schoolplus.office.services.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.schoolplus.office.config.JwtPkiConfiguration;
import com.schoolplus.office.config.ServerConfiguration;
import com.schoolplus.office.security.SecurityUser;
import com.schoolplus.office.services.JwtService;
import com.schoolplus.office.web.exceptions.JWTException;
import com.schoolplus.office.web.models.ErrorDesc;
import com.schoolplus.office.web.models.TokenCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.IdGenerator;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtServiceImpl implements JwtService {

    private final IdGenerator idGenerator;
    private final ServerConfiguration serverConfiguration;
    private final JwtPkiConfiguration jwtPkiConfiguration;

    @Override
    public SignedJWT createJwt(TokenCommand tokenCommand) {
        SecurityUser securityUser = tokenCommand.getSecurityUser();

        Date notBeforeTime = tokenCommand.getNotBeforeTime() != null ?
                Date.from(LocalDateTime.now().plusMinutes(tokenCommand.getNotBeforeTime().toMinutes()).atZone(ZoneId.systemDefault()).toInstant()) :
                new Date();

        LocalDateTime expiryDateTime = LocalDateTime.now().plusMinutes(tokenCommand.getExpiryDateTime().toMinutes());

        String audience = tokenCommand.getAudience() == null ? serverConfiguration.getServerUrl() : tokenCommand.getAudience();

        JWTClaimsSet.Builder jwtClaimsSetBuilder = new JWTClaimsSet.Builder()
                .subject(securityUser.getId().toString())
                .jwtID(idGenerator.generateId().toString())
                .audience(audience)
                .issuer(serverConfiguration.getServerUrl())
                .issueTime(new Date())
                .notBeforeTime(notBeforeTime)
                .expirationTime(Date.from(expiryDateTime.atZone(ZoneId.systemDefault()).toInstant()))
                .claim("username", securityUser.getUsername());

        for (Map.Entry<String, Object> claim: tokenCommand.getClaims().entrySet()) {
            jwtClaimsSetBuilder.claim(claim.getKey(), claim.getValue());
        }

        JWTClaimsSet jwtClaimsSet = jwtClaimsSetBuilder.build();

        log.info("Access token is generated for the User [userId: {}]", securityUser.getId().toString());

        return signJWT(jwtClaimsSet);
    }

    @Override
    public SignedJWT signJWT(JWTClaimsSet jwtClaimsSet) {
        try {
            JWSHeader.Builder jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS256);
            jwsHeader.keyID(jwtPkiConfiguration.getPublicKey().getKeyID());

            SignedJWT signedJWT =  new SignedJWT(jwsHeader.build(), jwtClaimsSet);
            signedJWT.sign(jwtPkiConfiguration.getJwsSigner());

            return signedJWT;
        } catch (JOSEException ex) {
            log.warn("Error while signing jwt [message: {}]", ex.getMessage());
            throw new JWTException(ErrorDesc.SERVER_ERROR.getDesc());
        }
    }

    @Override
    public JWTClaimsSet parseAndValidate(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            signedJWT.verify(jwtPkiConfiguration.getJwsVerifier());

            return signedJWT.getJWTClaimsSet();
        } catch (JOSEException | ParseException ex) {
            log.warn("Error while parsing jwt [message: {}]", ex.getMessage());
            throw new JWTException(ErrorDesc.INVALID_TOKEN.getDesc());
        }
    }

    @Override
    public boolean validate(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.verify(jwtPkiConfiguration.getJwsVerifier());
        } catch (JOSEException | ParseException ex) {
            log.warn("Error while parsing jwt [message: {}]", ex.getMessage());
            throw new JWTException(ErrorDesc.INVALID_TOKEN.getDesc());
        }
    }


}
