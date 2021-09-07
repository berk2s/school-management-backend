package com.schoolplus.office.services.impl;

import com.schoolplus.office.domain.*;
import com.schoolplus.office.repository.ClassroomRepository;
import com.schoolplus.office.repository.GradeRepository;
import com.schoolplus.office.repository.OrganizationRepository;
import com.schoolplus.office.repository.UserRepository;
import com.schoolplus.office.services.ClassroomService;
import com.schoolplus.office.web.exceptions.*;
import com.schoolplus.office.web.mappers.ClassroomMapper;
import com.schoolplus.office.web.models.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ClassroomServiceImpl implements ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final GradeRepository gradeRepository;
    private final ClassroomMapper classroomMapper;

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:classrooms') || hasAuthority('read:classroom'))")
    @Override
    public List<ClassroomDto> getClassrooms(Pageable pageable) {
        Page<Classroom> classRooms = classroomRepository.findAll(pageable);
        return classroomMapper.classRoomToClassRoomDto(classRooms.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:classrooms') || hasAuthority('read:classroom'))")
    @Override
    public ClassroomDto getClassroom(Long classRoomId) {
        Classroom classRoom = classroomRepository.findById(classRoomId)
                .orElseThrow(() -> {
                    log.warn("Classroom with given id does not exists [classRoomId:{}]", classRoomId);
                    throw new ClassroomNotFoundException(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc());
                });

        return classroomMapper.classRoomToClassRoomDto(classRoom);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:classrooms') || hasAuthority('create:classroom'))")
    @Override
    public ClassroomDto createClassroom(CreatingClassroomDto creatingClassroom) {
        Classroom classRoom = new Classroom();

        if (creatingClassroom.getClassRoomId() != null)
            classRoom.setClassRoomTag(creatingClassroom.getClassRoomId());

        Organization organization = organizationRepository.findById(creatingClassroom.getOrganizationId())
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists [organizationId: {}]", creatingClassroom.getOrganizationId());
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        classRoom.setOrganization(organization);

        Grade grade = gradeRepository.findById(creatingClassroom.getGradeId())
                .orElseThrow(() -> {
                    log.warn("Grade with given id does not exists [gradeId: {}]", creatingClassroom.getGradeId());
                    throw new GradeNotFoundException(ErrorDesc.GRADE_NOT_FOUND.getDesc());
                });

        grade.addClassroom(classRoom);

        UUID teacherId = UUID.fromString(creatingClassroom.getAdvisorTeacher());

        Teacher teacher = (Teacher) userRepository.findById(teacherId)
                .orElseThrow(() -> {
                    log.warn("Teacher with given id does not exists [teacherId: {}]", teacherId.toString());
                    throw new TeacherNotFoundException(ErrorDesc.TEACHER_NOT_FOUND.getDesc());
                });

        teacher.addClassroom(classRoom);

        if (creatingClassroom.getStudents() != null && creatingClassroom.getStudents().size() != 0) {
            creatingClassroom.getStudents().forEach(_studentId -> {
                UUID studentId = UUID.fromString(_studentId);

                Student student = (Student) userRepository.findById(studentId)
                        .orElseThrow(() -> {
                            log.warn("Student with given id does not exists [studentId: {}]", studentId.toString());
                            throw new StudentNotFoundException(ErrorDesc.STUDENT_NOT_FOUND.getDesc());
                        });

                classRoom.addStudent(student);
            });
        }

        Classroom savedClassroom = classroomRepository.save(classRoom);

        log.info("Classroom has been created successfully [classRoomId: {}, performedBy: {}]", savedClassroom.getId(),
                SecurityContextHolder.getContext().getAuthentication().getName());

        return classroomMapper.classRoomToClassRoomDto(savedClassroom);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:classrooms') || hasAuthority('update:classroom'))")
    @Override
    public void updateClassroom(Long classRoomId, EditingClassroomDto editingClassroom) {
        Classroom classRoom = classroomRepository.findById(classRoomId)
                .orElseThrow(() -> {
                    log.warn("Classroom with given id does not exists [classRoomId:{}]", classRoomId);
                    throw new ClassroomNotFoundException(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc());
                });

        if (editingClassroom.getClassRoomTag() != null)
            classRoom.setClassRoomTag(editingClassroom.getClassRoomTag());

        if (editingClassroom.getOrganizationId() != null
                && !classRoom.getOrganization().getId().equals(editingClassroom.getOrganizationId())) {
            Organization organization = organizationRepository.findById(editingClassroom.getOrganizationId())
                    .orElseThrow(() -> {
                        log.warn("Organization with given id does not exists [organizationId: {}]", editingClassroom.getOrganizationId());
                        throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                    });

            classRoom.setOrganization(organization);
        }

        if (editingClassroom.getAdvisorTeacher() != null) {
            UUID advisorTeacherId = UUID.fromString(editingClassroom.getAdvisorTeacher());

            Teacher teacher = (Teacher) userRepository.findById(advisorTeacherId)
                    .orElseThrow(() -> {
                        log.warn("Teacher with given id does not exists [teacherId: {}]", advisorTeacherId.toString());
                        throw new TeacherNotFoundException(ErrorDesc.TEACHER_NOT_FOUND.getDesc());
                    });

            teacher.addClassroom(classRoom);
        }

        if (editingClassroom.getAddedStudents() != null && editingClassroom.getAddedStudents().size() != 0) {
            editingClassroom.getAddedStudents().forEach(_studentId -> {
                UUID studentId = UUID.fromString(_studentId);

                Student student = (Student) userRepository.findById(studentId)
                        .orElseThrow(() -> {
                            log.warn("Student with given id does not exists [studentId: {}]", studentId.toString());
                            throw new StudentNotFoundException(ErrorDesc.STUDENT_NOT_FOUND.getDesc());
                        });

                classRoom.addStudent(student);
            });
        }

        if (editingClassroom.getDeletedStudents() != null && editingClassroom.getDeletedStudents().size() != 0) {
            editingClassroom.getDeletedStudents().forEach(_studentId -> {
                UUID studentId = UUID.fromString(_studentId);

                Student student = (Student) userRepository.findById(studentId)
                        .orElseThrow(() -> {
                            log.warn("Student with given id does not exists [studentId: {}]", studentId.toString());
                            throw new StudentNotFoundException(ErrorDesc.STUDENT_NOT_FOUND.getDesc());
                        });

                classRoom.removeStudent(student);
            });
        }

        classroomRepository.save(classRoom);

        log.info("Classroom has been updated successfully [classRoomId: {}, performedBy: {}]", classRoomId,
                SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:classrooms') || hasAuthority('delete:classroom'))")
    @Override
    public void deleteClassroom(Long classRoomId) {
        if (!classroomRepository.existsById(classRoomId)) {
            log.warn("Classroom with given id does not exists [classRoomId: {}]", classRoomId);
            throw new ClassroomNotFoundException(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc());
        }

        classroomRepository.deleteById(classRoomId);

        log.info("Classroom has been updated deleted [classRoomId: {}, performedBy: {}]", classRoomId,
                SecurityContextHolder.getContext().getAuthentication().getName());
    }


}
