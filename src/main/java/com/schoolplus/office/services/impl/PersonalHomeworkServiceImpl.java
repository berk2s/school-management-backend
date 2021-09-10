package com.schoolplus.office.services.impl;

import com.schoolplus.office.domain.Lesson;
import com.schoolplus.office.domain.PersonalHomework;
import com.schoolplus.office.domain.Student;
import com.schoolplus.office.domain.Teacher;
import com.schoolplus.office.repository.LessonRepository;
import com.schoolplus.office.repository.PersonalHomeworkRepository;
import com.schoolplus.office.repository.UserRepository;
import com.schoolplus.office.services.PersonalHomeworkService;
import com.schoolplus.office.web.exceptions.LessonNotFoundException;
import com.schoolplus.office.web.exceptions.PersonalHomeworkNotFoundException;
import com.schoolplus.office.web.exceptions.StudentNotFoundException;
import com.schoolplus.office.web.exceptions.TeacherNotFoundException;
import com.schoolplus.office.web.mappers.PersonalHomeworkMapper;
import com.schoolplus.office.web.models.CreatingPersonalHomeworkDto;
import com.schoolplus.office.web.models.EditingPersonalHomeworkDto;
import com.schoolplus.office.web.models.ErrorDesc;
import com.schoolplus.office.web.models.PersonalHomeworkDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class PersonalHomeworkServiceImpl implements PersonalHomeworkService {

    private final PersonalHomeworkRepository personalHomeworkRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final PersonalHomeworkMapper personalHomeworkMapper;

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:personalhomeworks') || hasAuthority('read:personalhomework'))")
    @Override
    public PersonalHomeworkDto getPersonalHomework(Long personalHomeworkId) {
        PersonalHomework personalHomework = personalHomeworkRepository.findById(personalHomeworkId)
                .orElseThrow(() -> {
                    log.warn("Personal Homework with given id does not exists [personalHomeworkId: {}]", personalHomeworkId);
                    throw new PersonalHomeworkNotFoundException(ErrorDesc.PERSONAL_HOMEWORK_NOT_FOUND.getDesc());
                });

        return personalHomeworkMapper.personalHomeworkToPersonalHomeworkDto(personalHomework);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:personalhomeworks') || hasAuthority('read:personalhomeworks'))")
    @Override
    public List<PersonalHomeworkDto> getPersonalHomeworkByStudent(UUID studentId, Pageable pageable) {
        Student student = (Student) userRepository.findById(studentId)
                .orElseThrow(() -> {
                    log.warn("Student with given id does not exists [studentId: {}]", studentId);
                    throw new StudentNotFoundException(ErrorDesc.STUDENT_NOT_FOUND.getDesc());
                });

        Page<PersonalHomework> personalHomeworks = personalHomeworkRepository
                .findAllByStudent(student, pageable);

        return personalHomeworkMapper.personalHomeworkToPersonalHomeworkDto(personalHomeworks.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:personalhomeworks') || hasAuthority('read:personalhomeworks'))")
    @Override
    public List<PersonalHomeworkDto> getPersonalHomeworkByTeacher(UUID teacherId, Pageable pageable) {
        Teacher teacher = (Teacher) userRepository.findById(teacherId)
                .orElseThrow(() -> {
                    log.warn("Teacher with given id does not exists [teacherId: {}]", teacherId);
                    throw new TeacherNotFoundException(ErrorDesc.TEACHER_NOT_FOUND.getDesc());
                });

        Page<PersonalHomework> personalHomeworks = personalHomeworkRepository
                .findAllByTeacher(teacher, pageable);

        return personalHomeworkMapper.personalHomeworkToPersonalHomeworkDto(personalHomeworks.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:personalhomeworks') || hasAuthority('read:personalhomeworks'))")
    @Override
    public List<PersonalHomeworkDto> getPersonalHomeworkByLesson(Long lessonId, Pageable pageable) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> {
                    log.warn("Lesson with given id does not exists [lessonId: {}]", lessonId);
                    throw new LessonNotFoundException(ErrorDesc.LESSON_NOT_FOUND.getDesc());
                });

        Page<PersonalHomework> personalHomeworks = personalHomeworkRepository
                .findAllByLesson(lesson, pageable);

        return personalHomeworkMapper.personalHomeworkToPersonalHomeworkDto(personalHomeworks.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:personalhomeworks') || hasAuthority('create:personalhomework'))")
    @Override
    public PersonalHomeworkDto createPersonalHomework(CreatingPersonalHomeworkDto creatingPersonalHomework) {
        PersonalHomework personalHomework = new PersonalHomework();
        personalHomework.setPersonalHomeworkDescription(creatingPersonalHomework.getPersonalHomeworkDescription());
        personalHomework.setDueDate(creatingPersonalHomework.getDueDate());

        if (creatingPersonalHomework.getPersonalHomeworkName() != null) {
            personalHomework.setPersonalHomeworkName(creatingPersonalHomework.getPersonalHomeworkName());
        }

        Teacher teacher = (Teacher) userRepository.findById(UUID.fromString(creatingPersonalHomework.getTeacherId()))
                .orElseThrow(() -> {
                    log.warn("Teacher with given id does not exists [teacherId: {}]", creatingPersonalHomework.getTeacherId());
                    throw new TeacherNotFoundException(ErrorDesc.TEACHER_NOT_FOUND.getDesc());
                });

        personalHomework.setTeacher(teacher);

        Student student = (Student) userRepository.findById(UUID.fromString(creatingPersonalHomework.getStudentId()))
                .orElseThrow(() -> {
                    log.warn("Student with given id does not exists [studentId: {}]", creatingPersonalHomework.getStudentId());
                    throw new StudentNotFoundException(ErrorDesc.STUDENT_NOT_FOUND.getDesc());
                });

        personalHomework.setStudent(student);

        Lesson lesson = lessonRepository.findById(creatingPersonalHomework.getLessonId())
                .orElseThrow(() -> {
                    log.warn("Lesson with given id does not exists [lessonId: {}]", creatingPersonalHomework.getLessonId());
                    throw new LessonNotFoundException(ErrorDesc.LESSON_NOT_FOUND.getDesc());
                });

        personalHomework.setLesson(lesson);

        personalHomeworkRepository.save(personalHomework);

        log.info("The Personal Homework has been created successfully [personalHomeWorkId: {}, performedBy: {}]",
                personalHomework.getId(), SecurityContextHolder.getContext().getAuthentication().getName());

        return personalHomeworkMapper.personalHomeworkToPersonalHomeworkDto(personalHomework);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:personalhomeworks') || hasAuthority('create:personalhomework'))")
    @Override
    public void updatePersonalHomework(Long personalHomeworkId, EditingPersonalHomeworkDto editingPersonalHomework) {
        PersonalHomework personalHomework = personalHomeworkRepository.findById(personalHomeworkId)
                        .orElseThrow(() -> {
                            log.warn("Personal Homework with given id does not exists [personalHomeworkId: {}]", personalHomeworkId);
                            throw new PersonalHomeworkNotFoundException(ErrorDesc.PERSONAL_HOMEWORK_NOT_FOUND.getDesc());
                        });

        if (editingPersonalHomework.getPersonalHomeworkName() != null) {
            personalHomework.setPersonalHomeworkName(editingPersonalHomework.getPersonalHomeworkName());
        }

        if (editingPersonalHomework.getPersonalHomeworkDescription() != null) {
            personalHomework.setPersonalHomeworkDescription(editingPersonalHomework.getPersonalHomeworkDescription());
        }

        if (editingPersonalHomework.getDueDate() != null) {
            personalHomework.setDueDate(editingPersonalHomework.getDueDate());
        }

        if (editingPersonalHomework.getTeacherId() != null) {
            Teacher teacher = (Teacher) userRepository.findById(UUID.fromString(editingPersonalHomework.getTeacherId()))
                    .orElseThrow(() -> {
                        log.warn("Teacher with given id does not exists [teacherId: {}]", editingPersonalHomework.getTeacherId());
                        throw new TeacherNotFoundException(ErrorDesc.TEACHER_NOT_FOUND.getDesc());
                    });

            personalHomework.setTeacher(teacher);
        }

        if (editingPersonalHomework.getStudentId() != null) {
            Student student = (Student) userRepository.findById(UUID.fromString(editingPersonalHomework.getStudentId()))
                    .orElseThrow(() -> {
                        log.warn("Student with given id does not exists [studentId: {}]", editingPersonalHomework.getStudentId());
                        throw new StudentNotFoundException(ErrorDesc.STUDENT_NOT_FOUND.getDesc());
                    });

            personalHomework.setStudent(student);
        }

        if (editingPersonalHomework.getLessonId() != null) {
            Lesson lesson = lessonRepository.findById(editingPersonalHomework.getLessonId())
                    .orElseThrow(() -> {
                        log.warn("Lesson with given id does not exists [lessonId: {}]", editingPersonalHomework.getLessonId());
                        throw new LessonNotFoundException(ErrorDesc.LESSON_NOT_FOUND.getDesc());
                    });

            personalHomework.setLesson(lesson);
        }

        personalHomeworkRepository.save(personalHomework);

        log.info("The Personal Homework has been updated successfully [personalHomeworkId: {}, performedBy: {}]", personalHomeworkId,
                SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:personalhomeworks') || hasAuthority('create:personalhomework'))")
    @Override
    public void deletePersonalHomework(Long personalHomeworkId) {
        if (!personalHomeworkRepository.existsById(personalHomeworkId)) {
            log.warn("Personal Homework with given id does not exists [personalHomeworkId: {}]", personalHomeworkId);
            throw new PersonalHomeworkNotFoundException(ErrorDesc.PERSONAL_HOMEWORK_NOT_FOUND.getDesc());
        }

        personalHomeworkRepository.deleteById(personalHomeworkId);

        log.info("The Personal Homework has been deleted successfully [personalHomeworkId: {}, performedBy: {}]", personalHomeworkId,
                SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
