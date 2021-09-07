package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.Classroom;
import com.schoolplus.office.web.models.ClassroomDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.List;

@Mapper(uses = {TeacherMapper.class, StudentMapper.class, GradeMapper.class})
public interface ClassroomMapper {

    @Mappings({
            @Mapping(source = "id", target = "classRoomId"),
            @Mapping(source = "classRoomTag", target = "classRoomTag"),
            @Mapping(source = "advisorTeacher", target = "advisorTeacher"),
            @Mapping(source = "organization", target = "organization"),
            @Mapping(source = "grade", target = "grade", qualifiedByName = "WithoutDetails"),
            @Mapping(target = "students", qualifiedByName="WithoutParents"),
    })
    ClassroomDto classRoomToClassRoomDto(Classroom classRoom);

    @Mappings({
            @Mapping(source = "id", target = "classRoomId"),
            @Mapping(source = "classRoomTag", target = "classRoomTag"),
            @Mapping(target = "advisorTeacher", source = "advisorTeacher"),
            @Mapping(source = "organization", target = "organization"),
            @Mapping(source = "grade", target = "grade", qualifiedByName = "WithoutDetails"),
            @Mapping(target = "students", source="WithoutParents"),
    })
    List<ClassroomDto> classRoomToClassRoomDto(List<Classroom> classrooms);

    @Named("WithoutStudents")
    @Mappings({
            @Mapping(source = "id", target = "classRoomId"),
            @Mapping(source = "classRoomTag", target = "classRoomTag"),
            @Mapping(source = "advisorTeacher", target = "advisorTeacher"),
            @Mapping(source = "organization", target = "organization"),
            @Mapping(source = "grade", target = "grade"),
            @Mapping(source = "students", target = "students", ignore = true),
    })
    ClassroomDto classRoomToClassRoomDtoWithoutStudents(Classroom classRoom);

    @Named("WithoutStudentsList")
    @Mappings({
            @Mapping(source = "id", target = "classRoomId"),
            @Mapping(source = "classRoomTag", target = "classRoomTag"),
            @Mapping(source = "advisorTeacher", target = "advisorTeacher"),
            @Mapping(source = "organization", target = "organization"),
            @Mapping(source = "grade", target = "grade"),
            @Mapping(source = "students", target = "students", ignore = true),
    })
    List<ClassroomDto> classRoomToClassRoomDtoWithoutStudents(List<Classroom> classRoom);


    @Named("WithoutStudentsAndParents")
    @Mappings({
            @Mapping(source = "id", target = "classRoomId"),
            @Mapping(source = "classRoomTag", target = "classRoomTag"),
            @Mapping(source = "advisorTeacher", target = "advisorTeacher", ignore = true),
            @Mapping(source = "organization", target = "organization"),
            @Mapping(source = "grade", target = "grade", ignore = true),
            @Mapping(source = "students", target = "students", ignore = true),
    })
    ClassroomDto classRoomToClassRoomDtoWithoutStudentsAndParents(Classroom classRoom);

    @Named("WithoutStudentsAndParentsList")
    @Mappings({
            @Mapping(source = "id", target = "classRoomId"),
            @Mapping(source = "classRoomTag", target = "classRoomTag"),
            @Mapping(source = "advisorTeacher", target = "advisorTeacher", ignore = true),
            @Mapping(source = "organization", target = "organization"),
            @Mapping(source = "grade", target = "grade", ignore = true),
            @Mapping(source = "students", target = "students", ignore = true),
    })
    List<ClassroomDto> classRoomToClassRoomDtoWithoutStudentsAndParents(List<Classroom> classRoom);

}
