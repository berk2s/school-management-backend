package com.schoolplus.office.services;

import com.schoolplus.office.web.models.ContinuityDto;
import com.schoolplus.office.web.models.CreatingContinuityDto;
import com.schoolplus.office.web.models.EditingContinuityDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ContinuityService {

    ContinuityDto getContinuity(UUID continuityId);

    List<ContinuityDto> getContinuityBySyllabus(Long syllabusId, Pageable pageable);

    List<ContinuityDto> getContinuityByClassroom(Long classroomId, Pageable pageable);

    List<ContinuityDto> getContinuityByStudent(UUID studentId, Pageable pageable);

    List<ContinuityDto> getContinuityByOrganization(Long organizationId, Pageable pageable);

    ContinuityDto createContinuity(CreatingContinuityDto creatingContinuity);

    void editContinuity(UUID continuityId, EditingContinuityDto editingContinuity);

    void deleteContinuity(UUID continuityId);

}
