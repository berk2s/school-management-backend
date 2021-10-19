package com.schoolplus.office.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.schoolplus.office.services.JwtService;
import com.schoolplus.office.web.controllers.authentication.LoginController;
import com.schoolplus.office.web.controllers.authentication.TokenController;
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
import java.util.List;


@RequiredArgsConstructor
@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final SecurityUserDetailsService securityUserDetailsService;

    private final List<String> skipUrls = List.of(LoginController.ENDPOINT,
            TokenController.ENDPOINT,
            "/swagger-ui.html",
            "/swagger-ui",
            "/api-docs");

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            final String headers = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);

            if ("OPTIONS".equalsIgnoreCase(httpServletRequest.getMethod())) {
                httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
                httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
                httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
                httpServletResponse.setHeader("Access-Control-Allow-Headers", "x-requested-with, authorization, Content-Type, Authorization, credential, X-XSRF-TOKEN");
                httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(httpServletRequest, httpServletResponse);
                return;
            }

            if(StringUtils.isEmpty(headers) || !headers.startsWith("Bearer ")) {
                httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value());
                return;
            }

            final String accessToken = headers.split(" ")[1].trim();

            if (!jwtService.validate(accessToken)) {
                httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value());
                return;
            }

            JWTClaimsSet jwtClaimsSet = jwtService.parseAndValidate(accessToken);

            Date date = jwtClaimsSet.getExpirationTime();
            Instant now = Instant.now();

            if (date.toInstant().isBefore(now)) {
                httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value());
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
            httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value());
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return skipUrls.stream().anyMatch(skipUrl -> request.getRequestURI().startsWith(skipUrl));
    }
}
