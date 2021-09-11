package com.schoolplus.office.services.impl;

import com.schoolplus.office.domain.Lesson;
import com.schoolplus.office.domain.Organization;
import com.schoolplus.office.repository.LessonRepository;
import com.schoolplus.office.repository.OrganizationRepository;
import com.schoolplus.office.services.LessonService;
import com.schoolplus.office.web.exceptions.LessonNotFoundException;
import com.schoolplus.office.web.exceptions.OrganizationNotFoundException;
import com.schoolplus.office.web.mappers.LessonMapper;
import com.schoolplus.office.web.models.CreatingLessonDto;
import com.schoolplus.office.web.models.EditingLessonDto;
import com.schoolplus.office.web.models.ErrorDesc;
import com.schoolplus.office.web.models.LessonDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final OrganizationRepository organizationRepository;
    private final LessonMapper lessonMapper;

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:lessons') || hasAuthority('read:lessons'))")
    @Override
    public List<LessonDto> getLessonsByOrganization(Long organizationId, Pageable pageable) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists [organizationId: {}]", organizationId);
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        Page<Lesson> lessons = lessonRepository.findAllByOrganization(organization, pageable);

        return lessonMapper.lessonToLessonDto(lessons.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:lessons') || hasAuthority('read:lessons'))")
    @Override
    public List<LessonDto> getLessons(Pageable pageable) {
        Page<Lesson> lessons = lessonRepository.findAll(pageable);

        return lessonMapper.lessonToLessonDto(lessons.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:lessons') || hasAuthority('read:lesson'))")
    @Override
    public LessonDto getLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> {
                    log.warn("Lesson with given id does not exists [lessonId: {}]", lessonId);
                    throw new LessonNotFoundException(ErrorDesc.LESSON_NOT_FOUND.getDesc());
                });

        return lessonMapper.lessonToLessonDto(lesson);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:lessons') || hasAuthority('create:lesson'))")
    @Override
    public LessonDto createLesson(CreatingLessonDto creatingLesson) {
        Lesson lesson = new Lesson();
        lesson.setLessonName(creatingLesson.getLessonName());

        Organization organization = organizationRepository.findById(creatingLesson.getOrganizationId())
                .orElseThrow(() -> {
                    log.warn("Organization with given id does not exists [organizationId: {}]", creatingLesson.getOrganizationId());
                    throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                });

        lesson.setOrganization(organization);

        lessonRepository.save(lesson);

        log.info("The Lesson has been created successfully [lessonId: {}, performedBy: {}]",
                lesson.getId(), SecurityContextHolder.getContext().getAuthentication().getName());

        return lessonMapper.lessonToLessonDto(lesson);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:lessons') || hasAuthority('update:lesson'))")
    @Override
    public void updateLesson(Long lessonId, EditingLessonDto editingLesson) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> {
                    log.warn("Lesson with given id does not exists [lessonId: {}]", lessonId);
                    throw new LessonNotFoundException(ErrorDesc.LESSON_NOT_FOUND.getDesc());
                });

        if (editingLesson.getLessonName() != null) {
            lesson.setLessonName(editingLesson.getLessonName());
        }

        if (editingLesson.getOrganizationId() != null) {
            Organization organization = organizationRepository.findById(editingLesson.getOrganizationId())
                    .orElseThrow(() -> {
                        log.warn("Organization with given id does not exists [organizationId: {}]", editingLesson.getOrganizationId());
                        throw new OrganizationNotFoundException(ErrorDesc.ORGANIZATION_NOT_FOUND.getDesc());
                    });

            lesson.setOrganization(organization);
        }

        lessonRepository.save(lesson);

        log.info("The Lesson has been updated successfully [lessonId: {}, performedBy: {}]",
                lessonId, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') && (hasAuthority('manage:lessons') || hasAuthority('delete:lesson'))")
    @Override
    public void deleteLesson(Long lessonId) {
        if (!lessonRepository.existsById(lessonId)) {
            log.warn("Lesson with given id does not exists [lessonId: {}]", lessonId);
            throw new LessonNotFoundException(ErrorDesc.LESSON_NOT_FOUND.getDesc());
        }

        lessonRepository.deleteById(lessonId);

        log.info("The Lesson has been deleted successfully [lessonId: {}, performedBy: {}]",
                lessonId, SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
