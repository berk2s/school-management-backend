package com.schoolplus.office.security;

import com.schoolplus.office.domain.User;
import com.schoolplus.office.repository.UserRepository;
import com.schoolplus.office.web.models.ErrorDesc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public SecurityUser loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found by given username [username: {}]", username);
                    throw new UsernameNotFoundException(ErrorDesc.BAD_CREDENTIALS.getDesc());
                });

        return new SecurityUser(user);
    }
}
