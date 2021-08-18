package com.schoolplus.office.security;

import com.schoolplus.office.web.models.ErrorDesc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserAuthenticationProvider implements AuthenticationProvider {

    private final SecurityUserDetailsService securityUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        SecurityUser securityUser =
                securityUserDetailsService.loadUserByUsername(username);

        if(passwordEncoder.matches(password, securityUser.getPassword())) {
            return new UsernamePasswordAuthenticationToken(username,
                    password, securityUser.getAuthorities());
        } else  {
            log.warn("Passwords are not matching [username: {}]", username);
            throw new BadCredentialsException(ErrorDesc.BAD_CREDENTIALS.getDesc());
        }
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
