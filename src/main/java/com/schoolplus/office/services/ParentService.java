package com.schoolplus.office.services;

import com.schoolplus.office.web.models.CreatingParentDto;
import com.schoolplus.office.web.models.EditingParentDto;
import com.schoolplus.office.web.models.ParentDto;

import java.util.UUID;

public interface ParentService {

    ParentDto getParent(UUID parentId);

    ParentDto createParent(CreatingParentDto creatingParent);

    void updateParent(UUID parentId, EditingParentDto editingParent);

}
