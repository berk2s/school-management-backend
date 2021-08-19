package com.schoolplus.office.security;

import com.schoolplus.office.domain.Authority;
import com.schoolplus.office.domain.Role;
import com.schoolplus.office.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SecurityUser implements UserDetails {

    private final User user;

    public SecurityUser(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();

        user.getAuthorities().forEach(authority -> grantedAuthorities.add(new SimpleGrantedAuthority(authority.getAuthorityName())));
        user.getRoles().forEach(role -> grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName().toUpperCase())));

        return grantedAuthorities;
    }

    public UUID getId() {
        return user.getId();
    }

    public List<Role> getPureRoles() {
        return new ArrayList<>(user.getRoles());
    }

    public List<Authority> getPureAuthorities() {
        return new ArrayList<>(user.getAuthorities());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}
