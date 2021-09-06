package com.schoolplus.office.services.impl;

import com.schoolplus.office.domain.Grade;
import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.domain.Student;
import com.schoolplus.office.domain.Teacher;
import com.schoolplus.office.repository.GradeRepository;
import com.schoolplus.office.repository.OrganizationRepository;
import com.schoolplus.office.repository.UserRepository;
import com.schoolplus.office.services.GradeService;
import com.schoolplus.office.utils.GradeUtils;
import com.schoolplus.office.web.exceptions.GradeNotFoundException;
import com.schoolplus.office.web.exceptions.OrganizationNotFoundException;
import com.schoolplus.office.web.exceptions.StudentNotFoundException;
import com.schoolplus.office.web.exceptions.TeacherNotFoundException;
import com.schoolplus.office.web.mappers.GradeMapper;
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
public class GradeServiceImpl implements GradeService {

    private final GradeRepository gradeRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final GradeMapper gradeMapper;

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:grades') || hasAuthority('read:grade'))")
    @Override
    public List<GradeDto> getGrades(Pageable pageable) {
        Page<Grade> grades = gradeRepository.findAll(pageable);
        return gradeMapper.gradeToGradeDto(grades.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:grades') || hasAuthority('read:grade'))")
    @Override
    public GradeDto getGrade(Long gradeId) {
        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> {
                    log.warn("Grade with given id does not exists [gradeId:{}]", gradeId);
                    throw new GradeNotFoundException(ErrorDesc.GRADE_NOT_FOUND.getDesc());
                });

        return gradeMapper.gradeToGradeDto(grade);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:grades') || hasAuthority('create:grade'))")
    @Override
    public GradeDto createGrade(CreatingGradeDto creatingGrade) {
        Grade grade = new Grade();
        grade.setGradeType(GradeUtils.levelConverter(creatingGrade.getGradeLevel()));
        grade.setGradeLevel(creatingGrade.getGradeLevel());

        if (creatingGrade.getGradeTag() != null)
            grade.setGradeTag(creatingGrade.getGradeTag());

        Organization organization = organizationRepository.findById(creatingGrade.getOrganizationId())
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists [organizationId: {}]", creatingGrade.getOrganizationId());
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        grade.setOrganization(organization);

        UUID teacherId = UUID.fromString(creatingGrade.getAdvisorTeacher());

        Teacher teacher = (Teacher) userRepository.findById(teacherId)
                .orElseThrow(() -> {
                    log.warn("Teacher with given id does not exists [teacherId: {}]", teacherId.toString());
                    throw new TeacherNotFoundException(ErrorDesc.TEACHER_NOT_FOUND.getDesc());
                });

        teacher.addGrade(grade);

        if (creatingGrade.getStudents() != null && creatingGrade.getStudents().size() != 0) {
            creatingGrade.getStudents().forEach(_studentId -> {
                UUID studentId = UUID.fromString(_studentId);

                Student student = (Student) userRepository.findById(studentId)
                        .orElseThrow(() -> {
                            log.warn("Student with given id does not exists [studentId: {}]", studentId.toString());
                            throw new StudentNotFoundException(ErrorDesc.STUDENT_NOT_FOUND.getDesc());
                        });

                grade.addStudent(student);
            });
        }

        Grade savedGrade = gradeRepository.save(grade);

        log.info("Grade has been created successfully [gradeId: {}, performedBy: {}]", savedGrade.getId(),
                SecurityContextHolder.getContext().getAuthentication().getName());

        return gradeMapper.gradeToGradeDto(savedGrade);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:grades') || hasAuthority('update:grade'))")
    @Override
    public void updateGrade(Long gradeId, EditingGradeDto editingGrade) {
        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> {
                   log.warn("Grade with given id does not exists [gradeId:{}]", gradeId);
                   throw new GradeNotFoundException(ErrorDesc.GRADE_NOT_FOUND.getDesc());
                });

        if (editingGrade.getGradeTag() != null)
            grade.setGradeTag(editingGrade.getGradeTag());

        if (editingGrade.getGradeLevel() != null) {
            GradeLevel gradeLevel = GradeLevel.valueOf(editingGrade.getGradeLevel());
            grade.setGradeType(GradeUtils.levelConverter(gradeLevel));
            grade.setGradeLevel(gradeLevel);
        }

        if (editingGrade.getOrganizationId() != null
                && !grade.getOrganization().getId().equals(editingGrade.getOrganizationId())) {
            Organization organization = organizationRepository.findById(editingGrade.getOrganizationId())
                    .orElseThrow(() -> {
                        log.warn("Organization with given id does not exists [organizationId: {}]", editingGrade.getOrganizationId());
                        throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                    });

            grade.setOrganization(organization);
        }

        if (editingGrade.getAdvisorTeacher() != null) {
            UUID advisorTeacherId = UUID.fromString(editingGrade.getAdvisorTeacher());

            Teacher teacher = (Teacher) userRepository.findById(advisorTeacherId)
                    .orElseThrow(() -> {
                        log.warn("Teacher with given id does not exists [teacherId: {}]", advisorTeacherId.toString());
                        throw new TeacherNotFoundException(ErrorDesc.TEACHER_NOT_FOUND.getDesc());
                    });

            teacher.addGrade(grade);
        }

        if (editingGrade.getAddedStudents() != null && editingGrade.getAddedStudents().size() != 0) {
            editingGrade.getAddedStudents().forEach(_studentId -> {
                UUID studentId = UUID.fromString(_studentId);

                Student student = (Student) userRepository.findById(studentId)
                        .orElseThrow(() -> {
                            log.warn("Student with given id does not exists [studentId: {}]", studentId.toString());
                            throw new StudentNotFoundException(ErrorDesc.STUDENT_NOT_FOUND.getDesc());
                        });

                grade.addStudent(student);
            });
        }

        if (editingGrade.getDeletedStudents() != null && editingGrade.getDeletedStudents().size() != 0) {
            editingGrade.getDeletedStudents().forEach(_studentId -> {
                UUID studentId = UUID.fromString(_studentId);

                Student student = (Student) userRepository.findById(studentId)
                        .orElseThrow(() -> {
                            log.warn("Student with given id does not exists [studentId: {}]", studentId.toString());
                            throw new StudentNotFoundException(ErrorDesc.STUDENT_NOT_FOUND.getDesc());
                        });

                grade.removeStudent(student);
            });
        }

        gradeRepository.save(grade);

        log.info("Grade has been updated successfully [gradeId: {}, performedBy: {}]", gradeId,
                SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Override
    public void deleteGrade(Long gradeId) {
        if(!gradeRepository.existsById(gradeId)) {
            log.warn("Grade with given id does not exists [gradeId: {}]", gradeId);
            throw new GradeNotFoundException(ErrorDesc.GRADE_NOT_FOUND.getDesc());
        }

        gradeRepository.deleteById(gradeId);

        log.info("Grade has been updated deleted [gradeId: {}, performedBy: {}]", gradeId,
                SecurityContextHolder.getContext().getAuthentication().getName());
    }


}
