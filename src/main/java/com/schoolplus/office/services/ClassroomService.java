package com.schoolplus.office.services;

import com.schoolplus.office.web.models.ClassroomDto;
import com.schoolplus.office.web.models.CreatingClassroomDto;
import com.schoolplus.office.web.models.EditingClassroomDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClassroomService {

    Page<ClassroomDto> getClassroomsByOrganization(Long organizationId, Pageable pageable, String search);

    List<ClassroomDto> getClassrooms(Pageable pageable);

    ClassroomDto getClassroom(Long classRoomId);

    ClassroomDto createClassroom(CreatingClassroomDto creatingClassroom);

    void updateClassroom(Long classRoomId, EditingClassroomDto editingClassroom);

    void deleteClassroom(Long classRoomId);

}
