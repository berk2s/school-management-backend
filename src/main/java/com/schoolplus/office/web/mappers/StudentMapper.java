package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.Authority;
import com.schoolplus.office.domain.Role;
import com.schoolplus.office.domain.Student;
import com.schoolplus.office.web.models.StudentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(imports = {UUID.class}, uses = {ParentMapper.class, GradeMapper.class})
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
            @Mapping(target = "parents", source = "parents"),
            @Mapping(target = "grade", qualifiedByName = "WithoutStudents"),
    })
    StudentDto studentToStudentDto(Student student);

    @Named("WithoutParents")
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
            @Mapping(target = "parents", source = "parents", ignore = true),
            @Mapping(target = "grade", qualifiedByName = "WithoutStudents"),
    })
    StudentDto studentToStudentDtoWithoutParents(Student student);

    @Named("ForAppointment")
    @Mappings({
            @Mapping(target = "userId", expression = "java( student.getId().toString() )"),
            @Mapping(target = "firstName", expression = "java( student.getFirstName() )"),
            @Mapping(target = "lastName", expression = "java( student.getLastName() )"),
            @Mapping(target = "parents", source = "parents", ignore = true),
            @Mapping(target = "grade", qualifiedByName = "WithoutStudents"),
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
    })
    StudentDto studentToStudentDtoForAppointment(Student student);

    default String mapAuthority(Authority authority){
        return authority != null ? authority.getAuthorityName() : null;
    }

    default String mapRoles(Role role){
        return role != null ? role.getRoleName() : null;
    }

}
