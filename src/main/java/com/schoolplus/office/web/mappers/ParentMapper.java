package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.Authority;
import com.schoolplus.office.domain.Parent;
import com.schoolplus.office.domain.Role;
import com.schoolplus.office.web.models.ParentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

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


}
