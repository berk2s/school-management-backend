package com.schoolplus.office.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.schoolplus.office.services.JwtService;
import com.schoolplus.office.web.exceptions.JWTException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final SecurityUserDetailsService securityUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            final String headers = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);

            if(StringUtils.isEmpty(headers) || !headers.startsWith("Bearer ")) {
                filterChain.doFilter(httpServletRequest, httpServletResponse);
                return;
            }

            final String accessToken = headers.split(" ")[1].trim();

            if (!jwtService.validate(accessToken)) {
                httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }

            JWTClaimsSet jwtClaimsSet = jwtService.parseAndValidate(accessToken);

            Date date = jwtClaimsSet.getExpirationTime();
            Instant now = Instant.now();

            if (date.toInstant().isBefore(now)) {
                httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }

            String username = jwtClaimsSet.getStringClaim("username");

            SecurityUser securityUser =
                    securityUserDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());

            authenticationToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(httpServletRequest)
            );

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } catch (UsernameNotFoundException | JWTException | ParseException e) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }

        return;
    }
}
