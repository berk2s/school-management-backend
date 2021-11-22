package com.schoolplus.office.config;

import com.schoolplus.office.security.JWTFilter;
import com.schoolplus.office.security.SecurityUserDetailsService;
import com.schoolplus.office.security.UserAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        jsr250Enabled = true,
        securedEnabled = true
)
@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserAuthenticationProvider userAuthenticationProvider;
    private final SecurityUserDetailsService securityUserDetailsService;
    private final JWTFilter jwtFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(userAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().disable().csrf().disable();

        http.sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.userDetailsService(securityUserDetailsService);

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        http.authorizeRequests()
                .mvcMatchers("/swagger-ui.html").permitAll()
                .mvcMatchers("/swagger-ui").permitAll()
                .mvcMatchers("/swagger-ui/**").permitAll()
                .mvcMatchers("/api-docs").permitAll()
                .mvcMatchers("/api-docs/**").permitAll()
                .mvcMatchers("/login").permitAll()
                .mvcMatchers("/token").permitAll()
                .mvcMatchers("/metadata").permitAll()
                .anyRequest().authenticated();

    }
}
