package com.schoolplus.office.bootstrap;

import com.schoolplus.office.domain.Authority;
import com.schoolplus.office.domain.Role;
import com.schoolplus.office.domain.TeachingSubject;
import com.schoolplus.office.domain.User;
import com.schoolplus.office.repository.AuthorityRepository;
import com.schoolplus.office.repository.RoleRepository;
import com.schoolplus.office.repository.TeachingSubjectRepository;
import com.schoolplus.office.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Profile({"test", "local"})
@RequiredArgsConstructor
@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;
    private final TeachingSubjectRepository teachingSubjectRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        loadUsers();
    }

    @Transactional
    private void loadUsers() {
        TeachingSubject teachingSubject = new TeachingSubject();
        teachingSubject.setSubjectName("Matematik");

        teachingSubjectRepository.save(teachingSubject);

        Role role = new Role();
        role.setRoleName("STUDENT");

        Role role1 = new Role();
        role1.setRoleName("ADMIN");

        Role role2 = new Role();
        role2.setRoleName("USER");

        Authority authority = new Authority();
        authority.setAuthorityName("profile:manage");

        Authority authority1 = new Authority();
        authority1.setAuthorityName("profile:edit");

        Authority authority2 = new Authority();
        authority2.setAuthorityName("list:users");

        roleRepository.saveAll(List.of(role, role1, role2));
        authorityRepository.saveAll(List.of(authority, authority1, authority2));

        User user = new User();
        user.setUsername("username");
        user.setPassword(passwordEncoder.encode("password"));
        user.setIsAccountNonLocked(true);
        user.setIsAccountNonExpired(true);
        user.setIsCredentialsNonExpired(true);
        user.setIsEnabled(true);
        user.addRole(role);
        user.addRole(role1);
        user.addAuthority(authority);
        user.addAuthority(authority1);
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setEmail("email@email.com");
        user.setPhoneNumber("05553332211");

        userRepository.save(user);

        log.info("Initial User has been created");
    }

}
