package com.schoolplus.office.services.impl;

import com.schoolplus.office.security.UserAuthenticationProvider;
import com.schoolplus.office.services.LoginService;
import com.schoolplus.office.web.models.LoginRequestDto;
import com.schoolplus.office.web.models.LoginResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class LoginServiceImpl implements LoginService {

    private final UserAuthenticationProvider userAuthenticationProvider;

    @Override
    public LoginResponseDto authenticate(LoginRequestDto loginRequest) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                        loginRequest.getPassword());

        userAuthenticationProvider.authenticate(usernamePasswordAuthenticationToken);

        log.info("User has been logged successfully [username: {}]", loginRequest.getUsername());

        return LoginResponseDto.builder()
                .accessToken("access")
                .refreshToken("refresh")
                .build();
    }
}
