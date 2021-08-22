package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.Authority;
import com.schoolplus.office.domain.Role;
import com.schoolplus.office.domain.User;
import com.schoolplus.office.web.models.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(imports = {UUID.class})
public interface UserMapper {

    @Mappings({
            @Mapping(target = "userId", expression = "java( user.getId().toString() )"),
            @Mapping(source = "authorities", target = "authorities"),
            @Mapping(source = "roles", target = "roles"),
            @Mapping(source = "isAccountNonExpired", target = "isAccountNonExpired"),
            @Mapping(source = "isAccountNonLocked", target = "isAccountNonLocked"),
            @Mapping(source = "isCredentialsNonExpired", target = "isCredentialsNonExpired"),
            @Mapping(source = "isEnabled", target = "isEnabled"),
    })
    UserDto userToUserDto(User user);

    @Mappings({
            @Mapping(target = "userId", expression = "java( users.getId().toString() )"),
            @Mapping(source = "authorities", target = "authorities"),
            @Mapping(source = "roles", target = "roles"),
            @Mapping(source = "isAccountNonExpired", target = "isAccountNonExpired"),
            @Mapping(source = "isAccountNonLocked", target = "isAccountNonLocked"),
            @Mapping(source = "isCredentialsNonExpired", target = "isCredentialsNonExpired"),
            @Mapping(source = "isEnabled", target = "isEnabled"),
    })
    List<UserDto> userToUserDto(List<User> users);


    default String mapAuthority(Authority authority){
        return authority != null ? authority.getAuthorityName() : null;
    }

    default String mapRoles(Role role){
        return role != null ? role.getRoleName() : null;
    }


}
