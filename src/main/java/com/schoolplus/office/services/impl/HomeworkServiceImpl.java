package com.schoolplus.office.services.impl;

import com.schoolplus.office.annotations.CreatingEntity;
import com.schoolplus.office.annotations.DeletingEntity;
import com.schoolplus.office.annotations.ReadingEntity;
import com.schoolplus.office.annotations.UpdatingEntity;
import com.schoolplus.office.domain.Classroom;
import com.schoolplus.office.domain.Homework;
import com.schoolplus.office.domain.Syllabus;
import com.schoolplus.office.domain.Teacher;
import com.schoolplus.office.repository.ClassroomRepository;
import com.schoolplus.office.repository.HomeworkRepository;
import com.schoolplus.office.repository.SyllabusRepository;
import com.schoolplus.office.repository.UserRepository;
import com.schoolplus.office.services.HomeworkService;
import com.schoolplus.office.web.exceptions.ClassroomNotFoundException;
import com.schoolplus.office.web.exceptions.HomeworkNotFoundException;
import com.schoolplus.office.web.exceptions.SyllabusNotFoundException;
import com.schoolplus.office.web.exceptions.TeacherNotFoundException;
import com.schoolplus.office.web.mappers.HomeworkMapper;
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
public class HomeworkServiceImpl implements HomeworkService {

    private final HomeworkRepository homeworkRepository;
    private final UserRepository userRepository;
    private final ClassroomRepository classroomRepository;
    private final SyllabusRepository syllabusRepository;
    private final HomeworkMapper homeworkMapper;

    @ReadingEntity(domain = TransactionDomain.HOMEWORK, action = DomainAction.READ_HOMEWORKS, isList = true)
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:homeworks') || hasAuthority('read:homeworks'))")
    @Override
    public List<HomeworkDto> getHomeworks(Pageable pageable) {
        Page<Homework> homeworks = homeworkRepository.findAll(pageable);

        return homeworkMapper.homeworkToHomeworkDto(homeworks.getContent());
    }

    @ReadingEntity(domain = TransactionDomain.HOMEWORK, action = DomainAction.READ_HOMEWORKS_BY_CLASSROOM, isList = true)
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:homeworks') || hasAuthority('read:homeworks'))")
    @Override
    public List<HomeworkDto> getHomeworksByClassroom(Long classroomId, Pageable pageable) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> {
                    log.warn("Classroom with given id does not exists [classroomId: {}]", classroomId);
                    throw new ClassroomNotFoundException(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc());
                });

        Page<Homework> homeworks = homeworkRepository.findAllByClassroom(classroom, pageable);

        return homeworkMapper.homeworkToHomeworkDto(homeworks.getContent());
    }

    @ReadingEntity(domain = TransactionDomain.HOMEWORK, action = DomainAction.READ_HOMEWORKS_BY_TEAHCER, isList = true)
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:homeworks') || hasAuthority('read:homeworks'))")
    @Override
    public List<HomeworkDto> getHomeworksByTeacher(String teacherId, Pageable pageable) {
        Teacher teacher = (Teacher) userRepository.findById(UUID.fromString(teacherId))
                .orElseThrow(() -> {
                    log.warn("Teacher with given id does not exists [teacher: {}]", teacherId);
                    throw new TeacherNotFoundException(ErrorDesc.TEACHER_NOT_FOUND.getDesc());
                });

        Page<Homework> homeworks = homeworkRepository.findAllByTeacher(teacher, pageable);

        return homeworkMapper.homeworkToHomeworkDto(homeworks.getContent());
    }

    @ReadingEntity(domain = TransactionDomain.HOMEWORK, action = DomainAction.READ_HOMEWORKS_BY_SYLLABUS, isList = true)
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:homeworks') || hasAuthority('read:homeworks'))")
    @Override
    public List<HomeworkDto> getHomeworksBySyllabus(Long syllabusId, Pageable pageable) {
        Syllabus syllabus = syllabusRepository.findById(syllabusId)
                .orElseThrow(() -> {
                    log.warn("Syllabus with given id does not exists [syllabusId: {}]", syllabusId);
                    throw new SyllabusNotFoundException(ErrorDesc.SYLLABUS_NOT_FOUND.getDesc());
                });

        Page<Homework> homeworks = homeworkRepository.findAllBySyllabus(syllabus, pageable);

        return homeworkMapper.homeworkToHomeworkDto(homeworks.getContent());
    }

    @ReadingEntity(domain = TransactionDomain.HOMEWORK, action = DomainAction.READ_HOMEWORK)
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:homeworks') || hasAuthority('read:homework'))")
    @Override
    public HomeworkDto getHomework(Long homeworkId) {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> {
                    log.warn("Homework with given id does not exists [homeworkId: {}]", homeworkId);
                    throw new HomeworkNotFoundException(ErrorDesc.HOMEWORK_NOT_FOUND.getDesc());
                });

        return homeworkMapper.homeworkToHomeworkDto(homework);
    }

    @CreatingEntity(domain = TransactionDomain.HOMEWORK, action = DomainAction.CREATE_HOMEWORK)
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:homeworks') || hasAuthority('create:homework'))")
    @Override
    public HomeworkDto createHomework(CreatingHomeworkDto creatingHomework) {
        Homework homework = new Homework();
        homework.setHomeworkDescription(creatingHomework.getHomeworkDescription());
        homework.setDueDate(creatingHomework.getDueDate());

        Classroom classroom = classroomRepository.findById(creatingHomework.getClassroomId())
                .orElseThrow(() -> {
                    log.warn("Classroom with given id does not exists [classroomId: {}]", creatingHomework.getClassroomId());
                    throw new ClassroomNotFoundException(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc());
                });

        homework.setClassroom(classroom);

        Teacher teacher = (Teacher) userRepository.findById(UUID.fromString(creatingHomework.getTeacherId()))
                .orElseThrow(() -> {
                    log.warn("Teacher with given id does not exists [teacherId: {}]", creatingHomework.getTeacherId());
                    throw new TeacherNotFoundException(ErrorDesc.TEACHER_NOT_FOUND.getDesc());
                });

        homework.setTeacher(teacher);

        Syllabus syllabus = syllabusRepository.findById(creatingHomework.getSyllabusId())
                        .orElseThrow(() -> {
                            log.warn("Syllabus with given id does not exists [syllabusId: {}]", creatingHomework.getSyllabusId());
                            throw new SyllabusNotFoundException(ErrorDesc.SYLLABUS_NOT_FOUND.getDesc());
                        });

        homework.setSyllabus(syllabus);

        homeworkRepository.save(homework);

        log.info("The Homework has been created successfully [homeworkId: {}, performedBy: {}]", homework.getId(),
                SecurityContextHolder.getContext().getAuthentication().getName());

        return homeworkMapper.homeworkToHomeworkDto(homework);
    }

    @UpdatingEntity(domain = TransactionDomain.HOMEWORK, action = DomainAction.UPDATE_HOMEWORK, idArg = "homeworkId")
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:homeworks') || hasAuthority('edit:homework'))")
    @Override
    public void updateHomework(Long homeworkId, EditingHomeworkDto editingHomework) {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> {
                    log.warn("Homework with given id does not exists [homeworkId: {}]", homeworkId);
                    throw new HomeworkNotFoundException(ErrorDesc.HOMEWORK_NOT_FOUND.getDesc());
                });

        if (editingHomework.getHomeworkDescription() != null) {
            homework.setHomeworkDescription(editingHomework.getHomeworkDescription());
        }

        if (editingHomework.getDueDate() != null) {
            homework.setDueDate(editingHomework.getDueDate());
        }

        if (editingHomework.getClassroomId() != null) {
            Classroom classroom = classroomRepository.findById(editingHomework.getClassroomId())
                    .orElseThrow(() -> {
                        log.warn("Classroom with given id does not exists [classroomId: {}]", editingHomework.getClassroomId());
                        throw new ClassroomNotFoundException(ErrorDesc.CLASSROOM_NOT_FOUND.getDesc());
                    });

            homework.setClassroom(classroom);
        }

        if (editingHomework.getTeacherId() != null) {
            Teacher teacher = (Teacher) userRepository.findById(UUID.fromString(editingHomework.getTeacherId()))
                    .orElseThrow(() -> {
                        log.warn("Teacher with given id does not exists [teacherId: {}]", editingHomework.getTeacherId());
                        throw new TeacherNotFoundException(ErrorDesc.TEACHER_NOT_FOUND.getDesc());
                    });

            homework.setTeacher(teacher);
        }

        if (editingHomework.getSyllabusId() != null) {
            Syllabus syllabus = syllabusRepository.findById(editingHomework.getSyllabusId())
                    .orElseThrow(() -> {
                        log.warn("Syllabus with given id does not exists [syllabusId: {}]", editingHomework.getSyllabusId());
                        throw new SyllabusNotFoundException(ErrorDesc.SYLLABUS_NOT_FOUND.getDesc());
                    });

            homework.setSyllabus(syllabus);
        }

        homeworkRepository.save(homework);

        log.info("The Homework has been updated successfully [homeworkId: {}, performedBy: {}]", homeworkId,
                SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @DeletingEntity(domain = TransactionDomain.HOMEWORK, action = DomainAction.DELETE_HOMEWORK, idArg = "homeworkId")
    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:homeworks') || hasAuthority('delete:homework'))")
    @Override
    public void deleteHomework(Long homeworkId) {
        if (!homeworkRepository.existsById(homeworkId)) {
            log.warn("Homework with given id does not exists [homeworkId: {}]", homeworkId);
            throw new HomeworkNotFoundException(ErrorDesc.HOMEWORK_NOT_FOUND.getDesc());
        }

        homeworkRepository.deleteById(homeworkId);

        log.info("The Homework has been deleted successfully [homeworkId: {}, performedBy: {}]", homeworkId,
                SecurityContextHolder.getContext().getAuthentication().getName());
    }

}
