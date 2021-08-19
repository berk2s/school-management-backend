package com.schoolplus.office.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Getter
@Component
public class JwtPkiConfiguration {

    private RSAKey publicKey;

    private JWKSet jwkSet;

    private JWSSigner jwsSigner;

    private JWSVerifier jwsVerifier;

    @PostConstruct
    public void init() throws JOSEException {
        RSAKey rsaKey = new RSAKeyGenerator(2048).keyID("1").generate();

        this.publicKey = rsaKey.toPublicJWK();
        this.jwsSigner = new RSASSASigner(rsaKey);
        this.jwkSet = new JWKSet(this.publicKey);
        this.jwsVerifier = new RSASSAVerifier(this.publicKey);
    }

}
