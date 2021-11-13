package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.Authority;
import com.schoolplus.office.domain.Parent;
import com.schoolplus.office.domain.Role;
import com.schoolplus.office.web.models.ParentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(imports = {UUID.class}, uses = {StudentMapper.class})
public interface ParentMapper {

    @Mappings({
            @Mapping(target = "students", source = "students"),
            @Mapping(target = "userId", expression = "java( parent.getId().toString() )"),
            @Mapping(target = "firstName", expression = "java( parent.getFirstName() )"),
            @Mapping(target = "lastName", expression = "java( parent.getLastName() )"),
            @Mapping(target = "phoneNumber", expression = "java( parent.getPhoneNumber() )"),
            @Mapping(target = "email", expression = "java( parent.getEmail() )"),
            @Mapping(target = "isAccountNonExpired", expression = "java( parent.getIsAccountNonExpired() )"),
            @Mapping(target = "isAccountNonLocked", expression = "java( parent.getIsAccountNonLocked() )"),
            @Mapping(target = "isCredentialsNonExpired", expression = "java( parent.getIsCredentialsNonExpired() )"),
            @Mapping(target = "isEnabled", expression = "java( parent.getIsEnabled() )"),
    })
    ParentDto parentToParentDto(Parent parent);

    @Named("WithoutDetails")
    @Mappings({
            @Mapping(target = "userId", expression = "java( parent.getId().toString() )"),
            @Mapping(target = "firstName", expression = "java( parent.getFirstName() )"),
            @Mapping(target = "lastName", expression = "java( parent.getLastName() )"),
            @Mapping(target = "students", source = "students", ignore = true),
            @Mapping(target = "username", source = "username", ignore = true),
            @Mapping(target = "phoneNumber", source = "phoneNumber", ignore = true),
            @Mapping(target = "email", source = "email", ignore = true),
            @Mapping(target = "authorities", source = "authorities", ignore = true),
            @Mapping(target = "roles", source = "roles", ignore = true),
            @Mapping(target = "isEnabled", source = "isEnabled", ignore = true),
            @Mapping(target = "isAccountNonExpired", source = "isAccountNonExpired", ignore = true),
            @Mapping(target = "isAccountNonLocked", source = "isAccountNonLocked", ignore = true),
            @Mapping(target = "isCredentialsNonExpired", source = "isCredentialsNonExpired", ignore = true),
            @Mapping(target = "createdAt", source = "createdAt", ignore = true),
            @Mapping(target = "lastModifiedAt", source = "lastModifiedAt", ignore = true),
            @Mapping(target = "organization", source = "organization", ignore = true),
    })
    ParentDto parentToParentDtoWithoutStudents(Parent parent);



}
