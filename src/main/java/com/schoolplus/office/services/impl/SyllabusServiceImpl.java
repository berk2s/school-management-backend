package com.schoolplus.office.services.impl;

import com.schoolplus.office.domain.*;
import com.schoolplus.office.repository.*;
import com.schoolplus.office.services.SyllabusService;
import com.schoolplus.office.web.exceptions.*;
import com.schoolplus.office.web.mappers.SyllabusMapper;
import com.schoolplus.office.web.models.CreatingSyllabusDto;
import com.schoolplus.office.web.models.EditingSyllabusDto;
import com.schoolplus.office.web.models.ErrorDesc;
import com.schoolplus.office.web.models.SyllabusDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class SyllabusServiceImpl implements SyllabusService {

    private final SyllabusRepository syllabusRepository;
    private final OrganizationRepository organizationRepository;
    private final ClassroomRepository classroomRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final SyllabusMapper syllabusMapper;

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:syllabuses') || hasAuthority('read:syllabuses'))")
    @Override
    public List<SyllabusDto> getSyllabuses(Pageable pageable) {
        Page<Syllabus> syllabuses = syllabusRepository.findAll(pageable);

        return syllabusMapper.syllabusToSyllabusDto(syllabuses.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:syllabuses') || hasAuthority('read:syllabus'))")
    @Override
    public SyllabusDto getSyllabus(Long syllabusId) {
        Syllabus syllabus = syllabusRepository.findById(syllabusId)
                .orElseThrow(() -> {
                    log.warn("Syllabus with given id does not exists [syllabusId: {}]", syllabusId);
                    throw new SyllabusNotFoundException(ErrorDesc.SYLLABUS_NOT_FOUND.getDesc());
                });

        return syllabusMapper.syllabusToSyllabusDto(syllabus);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:syllabuses') || hasAuthority('read:syllabuses'))")
    @Override
    public List<SyllabusDto> getSyllabusByClassroom(Pageable pageable, Long classroomId, LocalDateTime startDate,
                                                    LocalDateTime endDate) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> {
                    log.warn("Classroom with given id does not exists [classRoomId: {}]", classroomId);
                    throw new ClassroomNotFoundException(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc());
                });

        if (startDate == null) {
            startDate = LocalDateTime.now();
        }

        if (endDate == null) {
            endDate = LocalDateTime.now().plusDays(7);
        }

        Page<Syllabus> syllabuses = syllabusRepository.findAllByClassroomAndSyllabusStartDateBetween(classroom, startDate, endDate, pageable);

        return syllabusMapper.syllabusToSyllabusDto(syllabuses.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:syllabuses') || hasAuthority('read:syllabuses'))")
    @Override
    public List<SyllabusDto> getSyllabusByLesson(Pageable pageable, Long lessonId, LocalDateTime startDate,
                                                 LocalDateTime endDate) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> {
                    log.warn("Lesson with given id does not exists [lessonId: {}]", lessonId);
                    throw new LessonNotFoundException(ErrorDesc.LESSON_NOT_FOUND.getDesc());
                });

        if (startDate == null) {
            startDate = LocalDateTime.now();
        }

        if (endDate == null) {
            endDate = LocalDateTime.now().plusDays(7);
        }

        Page<Syllabus> syllabuses = syllabusRepository.findAllByLessonAndSyllabusStartDateBetween(lesson, startDate, endDate, pageable);

        return syllabusMapper.syllabusToSyllabusDto(syllabuses.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:syllabuses') || hasAuthority('read:syllabuses'))")
    @Override
    public List<SyllabusDto> getSyllabusByTeacher(Pageable pageable, UUID teacherId, LocalDateTime startDate, LocalDateTime endDate) {
        Teacher teacher = (Teacher) userRepository.findById(teacherId)
                .orElseThrow(() -> {
                    log.warn("Teacher with given id does not exists [teacherId: {}]", teacherId);
                    throw new TeacherNotFoundException(ErrorDesc.TEACHER_NOT_FOUND.getDesc());
                });

        if (startDate == null) {
            startDate = LocalDateTime.now();
        }

        if (endDate == null) {
            endDate = LocalDateTime.now().plusDays(7);
        }

        Page<Syllabus> syllabuses = syllabusRepository.findAllByTeacherAndSyllabusStartDateBetween(teacher, startDate, endDate, pageable);

        return syllabusMapper.syllabusToSyllabusDto(syllabuses.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:syllabuses') || hasAuthority('read:syllabuses'))")
    @Override
    public List<SyllabusDto> getSyllabusByOrganization(Pageable pageable, Long organizationId, LocalDateTime startDate, LocalDateTime endDate) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists [organizationId: {}]", organizationId);
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        if (startDate == null) {
            startDate = LocalDateTime.now();
        }

        if (endDate == null) {
            endDate = LocalDateTime.now().plusDays(7);
        }

        Page<Syllabus> syllabuses = syllabusRepository.findAllByOrganizationAndSyllabusStartDateBetween(organization, startDate, endDate, pageable);

        return syllabusMapper.syllabusToSyllabusDto(syllabuses.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:syllabuses') || hasAuthority('write:syllabus'))")
    @Override
    public SyllabusDto createSyllabus(CreatingSyllabusDto creatingSyllabus) {
        Syllabus syllabus = new Syllabus();
        syllabus.setSyllabusNote(creatingSyllabus.getSyllabusNote());
        syllabus.setSyllabusStartDate(creatingSyllabus.getSyllabusStartDate());
        syllabus.setSyllabusEndDate(creatingSyllabus.getSyllabusEndDate());

        Classroom classroom = classroomRepository.findById(creatingSyllabus.getClassroomId())
                .orElseThrow(() -> {
                    log.warn("Classroom with given id does not exists [classRoomId: {}]", creatingSyllabus.getClassroomId());
                    throw new ClassroomNotFoundException(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc());
                });

        syllabus.setClassroom(classroom);

        Lesson lesson = lessonRepository.findById(creatingSyllabus.getLessonId())
                .orElseThrow(() -> {
                    log.warn("Lesson with given id does not exists [lessoNid: {}]", creatingSyllabus.getLessonId());
                    throw new LessonNotFoundException(ErrorDesc.LESSON_NOT_FOUND.getDesc());
                });

        syllabus.setLesson(lesson);

        Teacher teacher = (Teacher) userRepository.findById(UUID.fromString(creatingSyllabus.getTeacherId()))
                .orElseThrow(() -> {
                    log.warn("Teacher with given id does not exists [teacherId: {}]", creatingSyllabus.getTeacherId());
                    throw new TeacherNotFoundException(ErrorDesc.TEACHER_NOT_FOUND.getDesc());
                });

        syllabus.setTeacher(teacher);

        Organization organization = organizationRepository.findById(creatingSyllabus.getOrganizationId())
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists [organizationId: {}]", creatingSyllabus.getOrganizationId());
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        syllabus.setOrganization(organization);

        syllabusRepository.save(syllabus);

        log.info("The Syllabus has been created successfully [syllabusId: {}, performedBy: {}]", syllabus.getId(),
                SecurityContextHolder.getContext().getAuthentication().getName());

        return syllabusMapper.syllabusToSyllabusDto(syllabus);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:syllabuses') || hasAuthority('edit:syllabus'))")
    @Override
    public void editSyllabus(Long syllabusId, EditingSyllabusDto editingSyllabus) {
        Syllabus syllabus = syllabusRepository.findById(syllabusId)
                .orElseThrow(() -> {
                    log.warn("Syllabus with given id does not exists [syllabusId: {}]", syllabusId);
                    throw new SyllabusNotFoundException(ErrorDesc.SYLLABUS_NOT_FOUND.getDesc());
                });

        LocalDateTime startDate = editingSyllabus.getSyllabusStartDate() != null ?
                editingSyllabus.getSyllabusStartDate() : syllabus.getSyllabusStartDate();

        LocalDateTime endDate = editingSyllabus.getSyllabusEndDate() != null ?
                editingSyllabus.getSyllabusEndDate() : syllabus.getSyllabusEndDate();

        if (editingSyllabus.getSyllabusNote() != null) {
            syllabus.setSyllabusNote(editingSyllabus.getSyllabusNote());
        }

        if (editingSyllabus.getSyllabusStartDate() != null && startDate.isBefore(endDate)) {
            syllabus.setSyllabusStartDate(startDate);
        }

        if (editingSyllabus.getSyllabusEndDate() != null && endDate.isAfter(startDate)) {
            syllabus.setSyllabusEndDate(endDate);
        }

        if (editingSyllabus.getClassroomId() != null) {
            Classroom classroom = classroomRepository.findById(editingSyllabus.getClassroomId())
                    .orElseThrow(() -> {
                        log.warn("Classroom with given id does not exists [classRoomId: {}]", editingSyllabus.getClassroomId());
                        throw new ClassroomNotFoundException(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc());
                    });

            syllabus.setClassroom(classroom);
        }

        if (editingSyllabus.getLessonId() != null) {
            Lesson lesson = lessonRepository.findById(editingSyllabus.getLessonId())
                    .orElseThrow(() -> {
                        log.warn("Lesson with given id does not exists [lessoNid: {}]", editingSyllabus.getLessonId());
                        throw new LessonNotFoundException(ErrorDesc.LESSON_NOT_FOUND.getDesc());
                    });

            syllabus.setLesson(lesson);
        }

        if (editingSyllabus.getTeacherId() != null) {
            Teacher teacher = (Teacher) userRepository.findById(UUID.fromString(editingSyllabus.getTeacherId()))
                    .orElseThrow(() -> {
                        log.warn("Teacher with given id does not exists [teacherId: {}]", editingSyllabus.getTeacherId());
                        throw new TeacherNotFoundException(ErrorDesc.TEACHER_NOT_FOUND.getDesc());
                    });

            syllabus.setTeacher(teacher);
        }

        if (editingSyllabus.getOrganizationId() != null) {
            Organization organization = organizationRepository.findById(editingSyllabus.getOrganizationId())
                    .orElseThrow(() -> {
                        log.warn("Organization with given id does not exists [organizationId: {}]", editingSyllabus.getOrganizationId());
                        throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                    });

            syllabus.setOrganization(organization);
        }

        syllabusRepository.save(syllabus);

        log.info("The Syllabus has been updated successfully [syllabusId: {}, performedBy: {}]", syllabusId,
                SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:syllabuses') || hasAuthority('delete:syllabus'))")
    @Override
    public void deleteSyllabus(Long syllabusId) {
        if (!syllabusRepository.existsById(syllabusId)) {
            log.warn("Syllabus with given id does not exists [syllabusId: {}]", syllabusId);
            throw new SyllabusNotFoundException(ErrorDesc.SYLLABUS_NOT_FOUND.getDesc());
        }

        syllabusRepository.deleteById(syllabusId);

        log.info("The Syllabus has been deleted successfully [syllabusId: {}, performedBy: {}]", syllabusId,
                SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
