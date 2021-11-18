package com.schoolplus.office.services;

import com.schoolplus.office.web.models.CreatingParentDto;
import com.schoolplus.office.web.models.EditingParentDto;
import com.schoolplus.office.web.models.ParentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ParentService {

    Page<ParentDto> getParentsByOrganization(Long organizationId, Pageable pageable, String search);

    ParentDto getParent(UUID parentId);

    ParentDto createParent(CreatingParentDto creatingParent);

    void updateParent(UUID parentId, EditingParentDto editingParent);

    void deleteParent(UUID parentId);

}
