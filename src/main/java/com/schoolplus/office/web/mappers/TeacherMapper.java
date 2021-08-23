package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.Authority;
import com.schoolplus.office.domain.Role;
import com.schoolplus.office.domain.Teacher;
import com.schoolplus.office.web.models.TeacherDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

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

    default String mapAuthority(Authority authority){
        return authority != null ? authority.getAuthorityName() : null;
    }

    default String mapRoles(Role role){
        return role != null ? role.getRoleName() : null;
    }

}
