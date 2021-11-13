package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.Authority;
import com.schoolplus.office.domain.Role;
import com.schoolplus.office.domain.Teacher;
import com.schoolplus.office.web.models.TeacherDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(imports = {UUID.class}, uses = {TeachingSubjectMapper.class})
public interface TeacherMapper {

    @Mappings({
            @Mapping(target = "teachingSubjects", source = "teachingSubjects"),
            @Mapping(target = "userId", expression = "java( teacher.getId().toString() )"),
            @Mapping(target = "firstName", expression = "java( teacher.getFirstName() )"),
            @Mapping(target = "lastName", expression = "java( teacher.getLastName() )"),
            @Mapping(target = "phoneNumber", expression = "java( teacher.getPhoneNumber() )"),
            @Mapping(target = "email", expression = "java( teacher.getEmail() )"),
            @Mapping(target = "isAccountNonExpired", expression = "java( teacher.getIsAccountNonExpired() )"),
            @Mapping(target = "isAccountNonLocked", expression = "java( teacher.getIsAccountNonLocked() )"),
            @Mapping(target = "isCredentialsNonExpired", expression = "java( teacher.getIsCredentialsNonExpired() )"),
            @Mapping(target = "isEnabled", expression = "java( teacher.getIsEnabled() )"),
    })
    TeacherDto teacherToTeacherDto(Teacher teacher);

    @Named("WithoutDetails")
    @Mappings({
            @Mapping(target = "userId", expression = "java( teacher.getId().toString() )"),
            @Mapping(target = "firstName", expression = "java( teacher.getFirstName() )"),
            @Mapping(target = "lastName", expression = "java( teacher.getLastName() )"),
            @Mapping(target = "teachingSubjects", source = "teachingSubjects"),
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
    TeacherDto teacherToTeacherDtoForAppointment(Teacher teacher);

    default String mapAuthority(Authority authority){
        return authority != null ? authority.getAuthorityName() : null;
    }

    default String mapRoles(Role role){
        return role != null ? role.getRoleName() : null;
    }

}
