package com.schoolplus.office.domain;

import com.schoolplus.office.web.models.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "USER_TYPE")
@DiscriminatorValue("USER")
@Entity
public class User extends BaseEntity {

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "email", unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "USER_TYPE", insertable = false, updatable = false)
    private UserType userType;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, optional = false)
    private Organization organization;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {
            CascadeType.MERGE,
    })
    @JoinTable(name = "USER_ROLES",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = {
            CascadeType.MERGE
    })
    @JoinTable(name = "USER_AUTHORITIES",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id"))
    private Set<Authority> authorities = new HashSet<>();

    @Column(name = "is_account_non_expired")
    private Boolean isAccountNonExpired;

    @Column(name = "is_account_non_locked")
    private Boolean isAccountNonLocked;

    @Column(name = "is_credentials_non_expired")
    private Boolean isCredentialsNonExpired;

    @Column(name = "is_enabled")
    private Boolean isEnabled;

    public void addRole(Role role) {
        role.getUsers().add(this);
        roles.add(role);
    }

    public void addAuthority(Authority authority) {
        authority.getUsers().add(this);
        authorities.add(authority);
    }

    public void deleteRole(Role role) {
        if (roles.contains(role) && role.getUsers().contains(this)) {
            role.getUsers().remove(this);
            roles.remove(role);
        }
    }

    public void deleteAuthority(Authority authority) {
        if (authorities.contains(authority) && authority.getUsers().contains(this)) {
            authority.getUsers().remove(this);
            authorities.remove(authority);
        }
    }

}
