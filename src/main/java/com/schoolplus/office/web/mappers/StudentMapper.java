package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.Authority;
import com.schoolplus.office.domain.Role;
import com.schoolplus.office.domain.Student;
import com.schoolplus.office.web.models.StudentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.UUID;

@Mapper(uses = {UUID.class})
public interface StudentMapper {

    @Mappings({
            @Mapping(target = "userId", expression = "java( student.getId().toString() )"),
            @Mapping(target = "firstName", expression = "java( student.getFirstName() )"),
            @Mapping(target = "lastName", expression = "java( student.getLastName() )"),
            @Mapping(target = "phoneNumber", expression = "java( student.getPhoneNumber() )"),
            @Mapping(target = "email", expression = "java( student.getEmail() )"),
            @Mapping(target = "gradeType", expression = "java( student.getGradeType().getType() )"),
            @Mapping(target = "gradeLevel", expression = "java( student.getGradeLevel().getGradeYear() )"),
            @Mapping(target = "isAccountNonExpired", expression = "java( student.getIsAccountNonExpired() )"),
            @Mapping(target = "isAccountNonLocked", expression = "java( student.getIsAccountNonLocked() )"),
            @Mapping(target = "isCredentialsNonExpired", expression = "java( student.getIsCredentialsNonExpired() )"),
            @Mapping(target = "isEnabled", expression = "java( student.getIsEnabled() )"),
    })
    StudentDto studentToStudentDto(Student student);

    default String mapAuthority(Authority authority){
        return authority != null ? authority.getAuthorityName() : null;
    }

    default String mapRoles(Role role){
        return role != null ? role.getRoleName() : null;
    }

}
